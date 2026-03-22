package com.super_tunes.playlist_service.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
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
    private LocalDate createdAt;

    public Playlist(){}

    public Playlist(Long id,String title,Long userId,LocalDate createdAt){
        this.id=id;
        this.title=title;
        this.userId=userId;
        this.createdAt=createdAt;
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

    public LocalDate getCreatedAt(){
        return this.createdAt;
    }
}
