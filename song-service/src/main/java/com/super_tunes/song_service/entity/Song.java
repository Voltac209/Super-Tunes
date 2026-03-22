package com.super_tunes.song_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name="songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String artist;

    @Column
    private String album;

    @Column(nullable=false)
    private Integer durationInSeconds;

    public Song(){}

    public Song(Long id,String title, String artist, String album, Integer durationInSeconds){
        this.id=id;
        this.title=title;
        this.artist=artist;
        this.album=album;
        this.durationInSeconds=durationInSeconds;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long new_id){
        this.id=new_id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String new_title){
        this.title=new_title;
    }

    public String getArtist(){
        return artist;
    }

    public void setArtist(String new_artist){
        this.artist=new_artist;
    }

    public String getAlbum(){
        return album;
    }

    public void setAlbum(String new_album){
        this.album=new_album;
    }

    public Integer getDurationInSeconds(){
        return durationInSeconds;
    }

    public void setDurationInSeconds(Integer new_duration){
        this.durationInSeconds=new_duration;
    }
    
}
