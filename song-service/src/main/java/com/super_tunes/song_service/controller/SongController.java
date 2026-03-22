package com.super_tunes.song_service.controller;

import com.super_tunes.song_service.entity.Song;
import com.super_tunes.song_service.service.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    private final SongService songService;
    public SongController(SongService songService){
        this.songService=songService;
    }

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs(){
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id){
        return songService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Song> createSong(@RequestBody Song song){
        return ResponseEntity.status(HttpStatus.CREATED).body(songService.createSong(song));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable Long id,@RequestBody Song song){
        return ResponseEntity.ok(songService.updateSong(id,song));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id){
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

}
