//package Holify.holify.controller;
//
//import Holify.holify.entity.Song;
//import Holify.holify.repository.SongRepository;
//import Holify.holify.repository.UserArtistPreferenceRepository;
//import Holify.holify.repository.UserGenrePreferenceRepository;
//import com.google.firebase.auth.FirebaseAuth;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/feed")
//public class FeedController {
//    private final SongRepository songRepository;
//    private final UserGenrePreferenceRepository genrePreferenceRepository;
//    private final UserArtistPreferenceRepository artistPreferenceRepository;
//    private final FirebaseAuth firebaseAuth;
//    private static final Logger log = LoggerFactory.getLogger(FeedController.class);
//
//    public FeedController(
//        SongRepository songRepository,
//        UserGenrePreferenceRepository genrePreferenceRepository,
//        UserArtistPreferenceRepository artistPreferenceRepository,
//        FirebaseAuth firebaseAuth) {
//        this.songRepository = songRepository;
//        this.genrePreferenceRepository = genrePreferenceRepository;
//        this.artistPreferenceRepository = artistPreferenceRepository;
//        this.firebaseAuth = firebaseAuth;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Song>> getFeed(@RequestHeader("Authorization") String idToken) throws Exception {
//        String uidStr = firebaseAuth.verifyIdToken(idToken).getUid();
//        Long uid = Long.parseLong(uidStr);
//        List<UserGenrePreference> genrePrefs = genrePreferenceRepository.findByUserId(uid);
//        List<UserArtistPreference> artistPrefs = artistPreferenceRepository.findByUserId(uid);
//        List<String> genres = genrePrefs.stream().map(UserGenrePreference::getGenre).collect(Collectors.toList());
//        List<String> artists = artistPrefs.stream().map(UserArtistPreference::getArtist).collect(Collectors.toList());
//
//        List<Song> songs = songRepository.findAll().stream()
//            .filter(song -> genres.contains(song.getGenre()) || artists.contains(song.getArtist()))
//            .collect(Collectors.toList());
//        log.info("Fetched {} songs for user {}", songs.size(), uid);
//        return ResponseEntity.ok(songs);
//    }
//}