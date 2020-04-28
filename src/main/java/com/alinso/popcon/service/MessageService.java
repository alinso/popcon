package com.alinso.popcon.service;


import com.alinso.popcon.entity.DeletedConversation;
import com.alinso.popcon.entity.Message;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.message.ConversationDto;
import com.alinso.popcon.entity.dto.message.MessageDto;
import com.alinso.popcon.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.popcon.entity.enums.Gender;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.DeletedConversationRepository;
import com.alinso.popcon.repository.MessageRepository;
import com.alinso.popcon.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserService userService;

    @Autowired
    DeletedConversationRepository deletedConversationRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BlockService blockService;

    @Autowired
    NotificationService notificationService;

    Boolean exceedDailyConversationLimit(User writer, User reader){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date yesterday= calendar.getTime();

        Integer readerResponseCount = messageRepository.readerResposeCount(writer,reader);
        if(readerResponseCount>0)
            return false;

        if(reader.getGender()==Gender.MALE)
            return false;

        //how many new conversations user has started today
        List<User> todayConversationCountWrittenByUser = messageRepository.todayConversationCountByWrittenByUser(writer,yesterday);
        List<User>todayConversationCountReadByUser = messageRepository.todayConversationCountReadByUser(writer,yesterday);


        //he did not write anyting today
        if(todayConversationCountWrittenByUser==null)
            return false;

        //he has writter more than 3 times and did not get any response
        if(todayConversationCountWrittenByUser.size()>3 && todayConversationCountReadByUser==null)
            return true;

        //he has writter less than 3 times and did not get any response
        if(todayConversationCountWrittenByUser.size()<3 && todayConversationCountReadByUser==null)
            return false;


        if(todayConversationCountWrittenByUser.size()>(todayConversationCountReadByUser.size()+3))
            return true;

        return false;
    }

    public MessageDto send(MessageDto messageDto) {
        Message message = modelMapper.map(messageDto, Message.class);
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reader = userService.findEntityById(messageDto.getReader().getId());

        if (blockService.isThereABlock(reader.getId()))
            throw new UserWarningException("Erişim Yok");


        if (!writer.getPhoneVerified() && reader.getId()!=59)
            throw new UserWarningException("DM Göndermeden önce telefon numaranı onaylamalısın");


        if(exceedDailyConversationLimit(writer,reader)){
            throw  new UserWarningException("Mesaj özelliğini çok sık kullanıyorsun, 24 saat sonra tekrar dene");
        }


        message.setWriter(writer);
        message.setReader(reader);

        messageRepository.save(message);


        messageDto.setCreatedAt(DateUtil.dateToString(message.getCreatedAt(), "DD/MM HH:mm"));
        messageDto.setReader(userService.toDto(message.getReader()));

        notificationService.newMessage(message.getReader());
        return messageDto;
    }

    public List<MessageDto> getMessagesForReader(Long readerId) {

        User reader = userService.findEntityById(readerId);
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Message> messages = messageRepository.getByReaderWriter(reader, writer);

        messages = removeDeletedMessages(messages);

        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message message : messages) {

            MessageDto messageDto = new MessageDto();
            messageDto.setMessage(message.getMessage());
            messageDto.setReader(userService.toDto(message.getReader()));
            messageDto.setCreatedAt(DateUtil.dateToString(message.getCreatedAt(), "dd/MM/YYYY HH:mm"));
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

    public List<Message> removeDeletedMessages(List<Message> messages) {

        //we only check if the current user deleted
        //if other user deleted the conversation, current user still needs to see
        User eraser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Iterator<Message> i = messages.iterator();
        while (i.hasNext()) {



            Message message = i.next();
            Long oppositeId = getOppositeId(message, eraser);
            User oppositeUser = userService.findEntityById(oppositeId);

            DeletedConversation deletedConversation = deletedConversationRepository.findByUserIds(eraser,oppositeUser);
            //it means user did not deleted any conversations with this other user
            if (deletedConversation == null)
                continue;

            //otherwise we need to check if this message sennt before deletion or not
            if (message.getId() <= deletedConversation.getLatesMessagBeforeDelete().getId()) {
                i.remove();
            }
        }
        return messages;
    }

    private Long getOppositeId(Message message, User me) {
        Long oppositeId;
        if (message.getReader().getId() == me.getId()) {
            oppositeId = message.getWriter().getId();
        } else {
            oppositeId = message.getReader().getId();
        }
        return oppositeId;
    }


    public List<ConversationDto> getMyConversations(Integer pageNum) {
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable  = PageRequest.of(pageNum,10);

        //read sql dtos from database
        List<Message> latestMessageFromEachConversation = messageRepository.latestMessageFromEachConversation(me,pageable);

        latestMessageFromEachConversation = removeDeletedConversations(latestMessageFromEachConversation);


        //we wont get two way latest message of same conversation
        //so for every conversation we will have OPPOSITEID
        List<Long> oppositeIds = new ArrayList<>();

        List<ConversationDto> myConversationDtos = new ArrayList<>();
        for (Message message : latestMessageFromEachConversation) {


            //define the opposite id for every conversation
            Long oppositeId = getOppositeId(message, me);

            //if opposite id exists, it means that we have added last message of this conversation
            if (!oppositeIds.contains(oppositeId))
                oppositeIds.add(oppositeId);
            else
                continue;

//            if (blockService.isThereABlock(oppositeId))
//                continue;


            User oppositeUser = userService.findEntityById(oppositeId);

            ConversationDto conversationDto = new ConversationDto();
            conversationDto.setReader(null);
            conversationDto.setWriter(null);
            conversationDto.setLastMessage(message.getMessage());
            conversationDto.setProfileDto(userService.toDto(oppositeUser));

            myConversationDtos.add(conversationDto);
        }

      //  userEventService.messaesRead();
        return myConversationDtos;
    }

    public List<Message> removeDeletedConversations(List<Message> allMessages) {

        //we only check if the current user deleted
        //if other user deleted the conversation, current user still needs to see
        User eraser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Iterator<Message> i = allMessages.iterator();
        while (i.hasNext()) {

            Message message = i.next();
            //define the opposite id for every conversation
            Long oppositeId = getOppositeId(message, eraser);
            User oppositeUser = userService.findEntityById(oppositeId);

            DeletedConversation deletedConversation = deletedConversationRepository.findByUserIds(eraser, oppositeUser);
            //it means user did not deleted any conversations with this other user
            if (deletedConversation == null)
                continue;

            //otherwise we need to check if this message sennt before deletion or not
            if (message.getId() <= deletedConversation.getLatesMessagBeforeDelete().getId()) {
                i.remove();
            }
        }
        return allMessages;
    }



    public void deleteConversation(Long otherId) {
        User eraserUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User otherUSer = userService.findEntityById(otherId);


        DeletedConversation deletedConversation = deletedConversationRepository.findByUserIds(eraserUser, otherUSer);
        if (deletedConversation == null)
            deletedConversation = new DeletedConversation();

        deletedConversation.setEraserUser(eraserUser);
        deletedConversation.setOtherUser(otherUSer);

        //the latest id of these messages will be saved and messages before that id wont be shown to user
        List<Message> messagesToMark = messageRepository.getByReaderWriter(eraserUser, otherUSer);
        deletedConversation.setLatesMessagBeforeDelete(messagesToMark.get(messagesToMark.size() - 1));


        deletedConversationRepository.save(deletedConversation);
    }



    public void greetingMessageForNewUser(User reader) {

        Message message = new Message();

        //this will be changed in future

            message.setMessage("Selam \uD83D\uDC4B " +
                    "ilk kullanıcılarımızdan olduğun için teşekkürler. Fotoğraf savaşlarına hoşgeldin. Sadece harika fotoğraflardan oluşan bir platform yaratıyoruz. " +
                    "Portre, moda, spor, doğa gibi birçok alanda fotoğraflarını paylaşabiir ve diğer fotoğrafları oylayabilirsin." +
                    "Aklına takılan bir şey olursa yaz. Sağ alt manüdeki nasıl çalışır kısmını okumayı unutma, iyi eğlenceler \uD83C\uDF89");
        /////
        message.setReader(reader);
        message.setWriter(userService.findEntityById(Long.valueOf(59)));

        messageRepository.save(message);
    }

}






























