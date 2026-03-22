package com.super_tunes.song_service.repository;

import com.super_tunes.song_service.entity.Song;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song,Long> {}