const storageKey = "superTunesSession";
const demoSongs = [
    { title: "Lofi Study Loop", artist: "Super Tunes Studio", album: "Lofi Sessions", durationInSeconds: 200 },
    { title: "Night Drive Pulse", artist: "Super Tunes Studio", album: "Techno Motion", durationInSeconds: 203 },
    { title: "Forest Windscape", artist: "Super Tunes Studio", album: "Nature Flow", durationInSeconds: 167 },
    { title: "Soft Rain Echoes", artist: "Super Tunes Studio", album: "Nature Flow", durationInSeconds: 141 },
    { title: "Neon Bassline", artist: "Super Tunes Studio", album: "Techno Motion", durationInSeconds: 198 },
    { title: "Morning Ambient Glow", artist: "Super Tunes Studio", album: "Ambient Tones", durationInSeconds: 238 },
    { title: "City Pop Sketch", artist: "Super Tunes Studio", album: "Indie Colors", durationInSeconds: 231 },
    { title: "Acoustic Daybreak", artist: "Super Tunes Studio", album: "Calm Horizons", durationInSeconds: 174 }
];

const state = {
    session: loadSession(),
    songs: [],
    playlists: [],
    audioContext: null,
    playingSongId: null,
};

const ui = {
    songCount: document.querySelector("#song-count"),
    playlistCount: document.querySelector("#playlist-count"),
    sessionName: document.querySelector("#session-name"),
    sessionEmail: document.querySelector("#session-email"),
    playlistCreateButton: document.querySelector("#playlist-create-button"),
    playlistsList: document.querySelector("#playlists-list"),
    toast: document.querySelector("#toast"),
};

const templates = {
    song: document.querySelector("#song-template"),
    playlist: document.querySelector("#playlist-template"),
};

bind("#signup-form", "submit", handleSignup);
bind("#login-form", "submit", handleLogin);
bind("#playlist-create-button", "click", handlePlaylistCreate);
bind("#logout-button", "click", logout);
bind("#refresh-button", "click", refreshAll);

renderSession();
refreshAll();

async function handleSignup(event) {
    event.preventDefault();
    const payload = formObject(event.currentTarget);
    const validationError = validateSignup(payload);
    if (validationError) {
        showToast(validationError);
        return;
    }
    await run(async () => {
        const session = await request("/auth/signup", { method: "POST", body: payload });
        setSession(session);
        event.currentTarget.reset();
        await refreshAll();
        showToast("Account created.");
    });
}

async function handleLogin(event) {
    event.preventDefault();
    const payload = formObject(event.currentTarget);
    const validationError = validateLogin(payload);
    if (validationError) {
        showToast(validationError);
        return;
    }
    await run(async () => {
        const session = await request("/auth/login", { method: "POST", body: payload });
        setSession(session);
        event.currentTarget.reset();
        await refreshAll();
        showToast("Logged in.");
    });
}

async function handlePlaylistCreate(event) {
    if (!state.session?.token) {
        showToast("Log in first.");
        return;
    }
    await run(async () => {
        await request("/api/playlists", {
            method: "POST",
            body: { title: `${state.session.username}'s Playlist ${state.playlists.filter((playlist) => playlist.userId === state.session.userId).length + 1}` },
            auth: true,
        });
        await refreshPlaylists();
        showToast("Playlist created.");
    });
}

function logout() {
    state.session = null;
    localStorage.removeItem(storageKey);
    renderSession();
    renderPlaylists();
    showToast("Logged out.");
}

async function refreshAll() {
    await Promise.all([refreshSongs(), refreshPlaylists()]);
}

async function refreshSongs() {
    const existingSongs = await request("/api/songs");
    state.songs = mergeDemoSongs(existingSongs);
    renderSongs();
    updateCounts();
}

async function refreshPlaylists() {
    const playlists = await request("/api/playlists");
    state.playlists = stableSortById(playlists);
    renderPlaylists();
    updateCounts();
}

function renderSession() {
    if (!ui.sessionName || !ui.sessionEmail) {
        return;
    }
    if (!state.session) {
        ui.sessionName.textContent = "No active session";
        ui.sessionEmail.textContent = "Write actions require a valid JWT.";
        syncPlaylistButton();
        return;
    }

    ui.sessionName.textContent = `${state.session.username} (#${state.session.userId})`;
    ui.sessionEmail.textContent = state.session.email;
    syncPlaylistButton();
}

function renderSongs() {
    updateCounts();
}

function renderPlaylists() {
    if (!state.session?.token) {
        renderCollection(ui.playlistsList, [], () => null, "Log in to view your playlists.");
        syncPlaylistButton();
        return;
    }

    const currentUserId = Number(state.session.userId);
    const visiblePlaylists = state.playlists.filter((playlist) => Number(playlist.userId) === currentUserId);
    renderCollection(ui.playlistsList, visiblePlaylists, (playlist) => {
        const node = templates.playlist.content.cloneNode(true);
        node.querySelector(".title").textContent = playlist.title;
        node.querySelector(".meta").textContent =
            `Playlist ID ${playlist.id} • Owner ${playlist.userId}`;

        node.querySelector(".delete-playlist").addEventListener("click", () => deletePlaylist(playlist.id));

        const select = node.querySelector(".song-select");
        populateSongSelect(select, playlist.songIds);
        node.querySelector(".add-song").addEventListener("click", () => {
            if (!select.value) {
                showToast("Select a song from the dropdown first.");
                return;
            }
            addSongToPlaylist(playlist.id, JSON.parse(select.value));
        });

        const tags = node.querySelector(".song-tags");
        if (!playlist.songIds.length) {
            tags.append(makeTag("No songs yet"));
        } else {
            playlist.songIds.forEach((songId) => {
                const normalizedSongId = Number(songId);
                const song = state.songs.find((item) => Number(item.id) === normalizedSongId);
                const label = song ? `${song.title} (#${songId})` : `Song ${songId}`;
                const entry = document.createElement("div");
                entry.className = "song-entry";

                const playTag = makeTag(label, true);
                if (Number(state.playingSongId) === normalizedSongId) {
                    playTag.classList.add("is-playing");
                }
                playTag.addEventListener("click", () => playSongPreview(song || { id: normalizedSongId, title: label }));
                entry.append(playTag);

                const removeButton = document.createElement("button");
                removeButton.type = "button";
                removeButton.className = "tag-remove";
                removeButton.textContent = "x";
                removeButton.title = "Remove from playlist";
                removeButton.addEventListener("click", () => removeSongFromPlaylist(playlist.id, normalizedSongId));
                entry.append(removeButton);

                tags.append(entry);
            });
        }

        return node;
    }, "No playlists yet.");
    syncPlaylistButton();
}

function renderCollection(target, items, mapper, emptyText) {
    if (!target) {
        return;
    }
    target.innerHTML = "";
    if (!items.length) {
        const empty = document.createElement("div");
        empty.className = "card";
        empty.textContent = emptyText;
        target.append(empty);
        return;
    }
    items.forEach((item) => target.append(mapper(item)));
}

function populateSongSelect(select, existingSongIds) {
    select.innerHTML = "";
    const existingIds = existingSongIds.map((songId) => Number(songId));

    const placeholder = document.createElement("option");
    placeholder.value = "";
    placeholder.textContent = state.songs.length ? "Choose a song" : "No songs available";
    select.append(placeholder);

    state.songs
        .filter((song) => !song.id || !existingIds.includes(Number(song.id)))
        .slice(0, 10)
        .forEach((song) => {
            const option = document.createElement("option");
            option.value = JSON.stringify(song);
            option.textContent = song.id
                ? `${song.title} - ${song.artist} (#${song.id})`
                : `${song.title} - ${song.artist}`;
            select.append(option);
        });
}

function makeTag(label, playable = false) {
    const tag = document.createElement("button");
    tag.type = "button";
    tag.className = "tag";
    if (playable) {
        tag.classList.add("playable");
        tag.title = "Play preview";
    }
    tag.textContent = label;
    return tag;
}

function updateCounts() {
    if (ui.songCount) {
        ui.songCount.textContent = String(state.songs.length);
    }
    if (ui.playlistCount) {
        ui.playlistCount.textContent = String(state.playlists.length);
    }
}

function syncPlaylistButton() {
    if (!ui.playlistCreateButton) {
        return;
    }

    if (!state.session?.token) {
        ui.playlistCreateButton.disabled = false;
        ui.playlistCreateButton.textContent = "Log in to create playlist";
        return;
    }

    ui.playlistCreateButton.disabled = false;
    ui.playlistCreateButton.textContent = "Create playlist";
}

async function deleteSong(songId) {
    await run(async () => {
        await request(`/api/songs/${songId}`, { method: "DELETE", auth: true });
        await Promise.all([refreshSongs(), refreshPlaylists()]);
        showToast(`Deleted song ${songId}.`);
    });
}

async function deletePlaylist(playlistId) {
    await run(async () => {
        await request(`/api/playlists/${playlistId}`, { method: "DELETE", auth: true });
        await refreshPlaylists();
        showToast(`Deleted playlist ${playlistId}.`);
    });
}

async function addSongToPlaylist(playlistId, songPayload) {
    await run(async () => {
        const songId = await ensureSongExists(songPayload);
        await request(`/api/playlists/${playlistId}/songs/${songId}`, { method: "POST", auth: true });
        await refreshPlaylists();
        showToast("Song added to playlist.");
    });
}

async function removeSongFromPlaylist(playlistId, songId) {
    await run(async () => {
        await request(`/api/playlists/${playlistId}/songs/${songId}`, { method: "DELETE", auth: true });
        await refreshPlaylists();
        showToast("Song removed from playlist.");
    });
}

async function run(action) {
    try {
        await action();
    } catch (error) {
        showToast(error.message);
    }
}

async function request(path, options = {}) {
    const headers = { "Content-Type": "application/json" };
    if (options.auth) {
        if (!state.session?.token) {
            throw new Error("Log in first.");
        }
        headers.Authorization = `Bearer ${state.session.token}`;
    }

    const response = await fetch(path, {
        method: options.method || "GET",
        headers,
        body: options.body ? JSON.stringify(options.body) : undefined,
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    const payload = text ? parseJson(text) : null;
    if (!response.ok) {
        throw new Error(messageFrom(payload, text, response.status));
    }
    return payload;
}

function setSession(session) {
    state.session = session;
    localStorage.setItem(storageKey, JSON.stringify(session));
    renderSession();
    renderPlaylists();
}

function loadSession() {
    const raw = localStorage.getItem(storageKey);
    return raw ? parseJson(raw) : null;
}

function formObject(form) {
    return Object.fromEntries(new FormData(form).entries());
}

function parseJson(text) {
    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

function messageFrom(payload, text, status) {
    if (typeof payload === "string" && payload) {
        return payload;
    }
    if (payload && typeof payload === "object") {
        if (Array.isArray(payload.errors) && payload.errors.length) {
            return payload.errors.join(" ");
        }
        if (payload.message) {
            return payload.message;
        }
        if (payload.error) {
            return payload.error;
        }
    }
    return text || `Request failed with status ${status}.`;
}

function validateSignup(payload) {
    if (!payload.username || payload.username.trim().length < 3) {
        return "Username must be at least 3 characters.";
    }
    if (!isValidEmail(payload.email)) {
        return "Enter a valid email address.";
    }
    if (!payload.password || payload.password.length < 8) {
        return "Password must be at least 8 characters.";
    }
    return "";
}

function validateLogin(payload) {
    if (!isValidEmail(payload.email)) {
        return "Enter a valid email address.";
    }
    if (!payload.password) {
        return "Password is required.";
    }
    return "";
}

function isValidEmail(value) {
    return typeof value === "string" && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim());
}

function mergeDemoSongs(existingSongs) {
    const keys = new Set(existingSongs.map(songKey));
    const missingDemos = demoSongs.filter((song) => !keys.has(songKey(song)));
    return [...existingSongs, ...missingDemos];
}

function songKey(song) {
    return [song.title, song.artist, song.album || "", song.durationInSeconds].join("::");
}

function stableSortById(items) {
    return [...items].sort((left, right) => left.id - right.id);
}

async function playSongPreview(song) {
    const audioContext = getAudioContext();
    if (audioContext.state === "suspended") {
        await audioContext.resume();
    }

    state.playingSongId = song.id || null;
    renderPlaylists();

    const now = audioContext.currentTime;
    const seed = songSeed(song);
    const notes = notePattern(seed);
    const gain = audioContext.createGain();
    gain.gain.setValueAtTime(0.0001, now);
    gain.gain.exponentialRampToValueAtTime(0.14, now + 0.03);
    gain.gain.exponentialRampToValueAtTime(0.0001, now + 1.9);
    gain.connect(audioContext.destination);

    notes.forEach((frequency, index) => {
        const oscillator = audioContext.createOscillator();
        oscillator.type = oscillatorType(song);
        oscillator.frequency.setValueAtTime(frequency, now + index * 0.28);
        oscillator.connect(gain);
        oscillator.start(now + index * 0.28);
        oscillator.stop(now + index * 0.28 + 0.24);
    });

    window.setTimeout(() => {
        state.playingSongId = null;
        renderPlaylists();
    }, 2100);
}

function getAudioContext() {
    if (!state.audioContext) {
        const AudioContextCtor = window.AudioContext || window.webkitAudioContext;
        state.audioContext = new AudioContextCtor();
    }
    return state.audioContext;
}

function songSeed(song) {
    return `${song.title}|${song.artist}|${song.album || ""}`
        .split("")
        .reduce((value, char) => value + char.charCodeAt(0), 0);
}

function notePattern(seed) {
    const scale = [220, 246.94, 261.63, 293.66, 329.63, 392, 440];
    return Array.from({ length: 6 }, (_, index) => {
        const offset = (seed + index * 3) % scale.length;
        return scale[offset];
    });
}

function oscillatorType(song) {
    const label = `${song.title} ${song.album || ""}`.toLowerCase();
    if (label.includes("techno") || label.includes("neon")) {
        return "sawtooth";
    }
    if (label.includes("nature") || label.includes("rain") || label.includes("forest")) {
        return "triangle";
    }
    if (label.includes("lofi") || label.includes("ambient")) {
        return "sine";
    }
    return "square";
}

async function ensureSongExists(songPayload) {
    if (songPayload.id) {
        return songPayload.id;
    }

    const existing = state.songs.find((song) => song.id && songKey(song) === songKey(songPayload));
    if (existing) {
        return existing.id;
    }

    const created = await request("/api/songs", {
        method: "POST",
        body: songPayload,
        auth: true,
    });
    await refreshSongs();
    return created.id;
}

function formatDuration(totalSeconds) {
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes}:${String(seconds).padStart(2, "0")}`;
}

let toastTimer;
function showToast(message) {
    if (!ui.toast) {
        return;
    }
    ui.toast.textContent = message;
    ui.toast.classList.add("visible");
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => ui.toast.classList.remove("visible"), 2600);
}

function bind(selector, eventName, handler) {
    const element = document.querySelector(selector);
    if (element) {
        element.addEventListener(eventName, handler);
    }
}
