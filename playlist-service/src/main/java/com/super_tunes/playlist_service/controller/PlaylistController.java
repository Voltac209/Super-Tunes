package com.super_tunes.playlist_service.controller;

import com.super_tunes.playlist_service.entity.Playlist;
import com.super_tunes.playlist_service.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;
    public PlaylistController(PlaylistService playlistService){
        this.playlistService=playlistService;
    }
    
    @GetMapping
    public ResponseEntity<List<Playlist>>getAllPlaylists(){
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable Long id){
        return playlistService.getPlaylistById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Playlist newPlaylist){
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistService.createPlaylist(newPlaylist));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id,@RequestBody Playlist newPlaylist){
        return ResponseEntity.ok(playlistService.updatePlaylist(id,newPlaylist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id){
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }
}
