package com.moxi.lyra.Mongo.Message;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@Document(collection = "Messages")

public class MongoMessage {
@Id
private String id;
private String sender;
private String receiver;
private String content;
private Date timestamp;

public MongoMessage(String sender, String receiver, String content) {
	this.sender = sender;
	this.receiver = receiver;
	this.content = content;
	this.timestamp = new Date();
}
private String getFormatedTimestamp() {
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
	return formatter.format(timestamp);
}
@Override
public String toString() {
	return "MongoMessage{" +
			"senderId='" + sender + '\'' +
			", receiverId='" + receiver + '\'' +
			", content='" + content + '\'' +
			", timestamp=" + getFormatedTimestamp() +
			'}';
}
}
