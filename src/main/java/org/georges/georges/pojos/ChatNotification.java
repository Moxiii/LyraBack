package org.georges.georges.pojos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String receiverName;
    private String content;
}
