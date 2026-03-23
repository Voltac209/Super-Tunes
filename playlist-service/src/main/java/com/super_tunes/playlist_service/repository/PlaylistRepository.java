package com.super_tunes.playlist_service.repository;

import com.super_tunes.playlist_service.entity.Playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlaylistRepository extends JpaRepository<Playlist,Long> {}
