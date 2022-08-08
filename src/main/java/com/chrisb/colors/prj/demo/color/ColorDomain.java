package com.chrisb.colors.prj.demo.color;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "colors")
public class ColorDomain {

    @Id
    private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    private String color;
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    private LocalDateTime timestamp;
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
