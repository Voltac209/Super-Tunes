package com.super_tunes.playlist_service.service;

import com.super_tunes.playlist_service.entity.Playlist;
import com.super_tunes.playlist_service.repository.PlaylistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository){
        this.playlistRepository=playlistRepository;
    }
    
    @Transactional(readOnly=true)
    public List<Playlist> getAllPlaylists(){
        return playlistRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Optional<Playlist> getPlaylistById(Long id){
        return playlistRepository.findById(id);
    }

    @Transactional
    public Playlist createPlaylist(Playlist newPlaylist){
        newPlaylist.setCreatedAt();
        newPlaylist.updatedAt();
        return playlistRepository.save(newPlaylist);
    }

    @Transactional
    public Playlist updatePlaylist(Long id,Playlist newPlaylist){
        Optional<Playlist> oldPlaylist=getPlaylistById(id);
        if (oldPlaylist.isPresent()){
            Playlist playlist=oldPlaylist.get();
            playlist.updateTitle(newPlaylist.getTitle());
            playlist.setUserId(newPlaylist.getUserId());
            playlist.updatedAt();
            return playlistRepository.save(playlist);
        }
        else throw new RuntimeException("No Playlist Found with id: "+id);
    }

    @Transactional
    public void deletePlaylist(Long id){
        if (!playlistRepository.existsById(id)) 
        throw new RuntimeException("Playlist Not Found with id: "+id);

        playlistRepository.deleteById(id);
    }


}
