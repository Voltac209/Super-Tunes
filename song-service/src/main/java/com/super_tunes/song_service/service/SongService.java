package com.super_tunes.song_service.service;

import com.super_tunes.song_service.entity.Song;
import com.super_tunes.song_service.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository){
        this.songRepository=songRepository;
    }

    @Transactional(readOnly=true)
    public List<Song> getAllSongs(){
        return songRepository.findAll();
    } 

    @Transactional(readOnly=true)
    public Optional<Song> findById(Long id){
        return songRepository.findById(id);
    }

    @Transactional
    public Song createSong(Song song){
        return songRepository.save(song);
    }

    @Transactional 
    public Song updateSong(Long id, Song updatedSong){
        return songRepository.findById(id).map(song -> {
            song.setTitle(updatedSong.getTitle());
            song.setAlbum(updatedSong.getAlbum());
            song.setArtist(updatedSong.getArtist());
            song.setDurationInSeconds(updatedSong.getDurationInSeconds());
            return songRepository.save(song);
        }).orElseThrow(() -> new RuntimeException("Song Not Found with ID: "+id));
    }

    @Transactional
    public void deleteSong(Long id){
        if (!songRepository.existsById(id)){
            throw new RuntimeException("No Soung found with id = "+id);
        }
        songRepository.deleteById(id);
    }
    
}
