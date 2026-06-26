package com.super_tunes.playlist_service.controller;

import com.super_tunes.playlist_service.entity.Playlist;
import com.super_tunes.playlist_service.service.JwtService;
import com.super_tunes.playlist_service.service.PlaylistService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final JwtService jwtService;

    public PlaylistController(PlaylistService playlistService, JwtService jwtService) {
        this.playlistService = playlistService;
        this.jwtService = jwtService;
    }
    
    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable Long id) {
        return playlistService.getPlaylistById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(
        @RequestBody Playlist newPlaylist,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(playlistService.createPlaylist(newPlaylist, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(
        @PathVariable Long id,
        @RequestBody Playlist newPlaylist,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(playlistService.updatePlaylist(id, newPlaylist, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(
        @PathVariable Long id,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        playlistService.deletePlaylist(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<Playlist> addSongToPlaylist(
        @PathVariable Long id,
        @PathVariable Long songId,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(playlistService.addSongToPlaylist(id, songId, userId));
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Playlist> removeSongFromPlaylist(
        @PathVariable Long id,
        @PathVariable Long songId,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(playlistService.removeSongFromPlaylist(id, songId, userId));
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return jwtService.extractUserId(authHeader.substring(7));
    }
}
