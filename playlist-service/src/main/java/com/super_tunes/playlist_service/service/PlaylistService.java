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

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Playlist> getPlaylistById(Long id) {
        return playlistRepository.findById(id);
    }

    @Transactional
    public Playlist createPlaylist(Playlist newPlaylist, Long userId) {
        newPlaylist.setUserId(userId);
        newPlaylist.setCreatedAt();
        newPlaylist.updatedAt();
        return playlistRepository.save(newPlaylist);
    }

    @Transactional
    public Playlist updatePlaylist(Long id, Playlist newPlaylist, Long userId) {
        Optional<Playlist> oldPlaylist = getPlaylistById(id);
        if (oldPlaylist.isPresent()) {
            Playlist playlist = oldPlaylist.get();
            if (!playlist.getUserId().equals(userId)) {
                throw new RuntimeException("You are not allowed to update this playlist");
            }
            playlist.updateTitle(newPlaylist.getTitle());
            playlist.updatedAt();
            return playlistRepository.save(playlist);
        }
        throw new RuntimeException("No Playlist Found with id: " + id);
    }

    @Transactional
    public void deletePlaylist(Long id, Long userId) {
        Playlist playlist = playlistRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Playlist Not Found with id: " + id));
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this playlist");
        }
        playlistRepository.deleteById(id);
    }

    @Transactional
    public Playlist addSongToPlaylist(Long id, Long songId, Long userId) {
        Playlist playlist = playlistRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Playlist Not Found with id: " + id));
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this playlist");
        }
        playlist.addSongId(songId);
        playlist.updatedAt();
        return playlistRepository.save(playlist);
    }

    @Transactional
    public Playlist removeSongFromPlaylist(Long id, Long songId, Long userId) {
        Playlist playlist = playlistRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Playlist Not Found with id: " + id));
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this playlist");
        }
        playlist.removeSongId(songId);
        playlist.updatedAt();
        return playlistRepository.save(playlist);
    }
}
