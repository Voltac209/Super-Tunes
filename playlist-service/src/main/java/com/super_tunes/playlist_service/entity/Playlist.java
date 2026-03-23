package com.super_tunes.playlist_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private Long userId;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @Column(nullable=false)
    private LocalDateTime updatedAt;

    public Playlist(){}

    public Playlist(Long id,String title,Long userId){
        this.id=id;
        this.title=title;
        this.userId=userId;
        this.createdAt=LocalDateTime.now();
        this.updatedAt=LocalDateTime.now();
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public String getTitle(){
        return title;
    }

    public void updateTitle(String new_title){
        this.title=new_title;
    }

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long newUserId){
        this.userId=newUserId;
    }

    public void setCreatedAt(){
        this.createdAt=LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }

    public void updatedAt(){
        this.updatedAt=LocalDateTime.now();
    }
}
