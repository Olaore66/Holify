//package Holify.holify.controller;
//
//import Holify.holify.entity.User;
//import Holify.holify.entity.UserArtistPreference;
//import Holify.holify.entity.UserGenrePreference;
//import Holify.holify.repository.SongRepository;
//import Holify.holify.repository.UserArtistPreferenceRepository;
//import Holify.holify.repository.UserGenrePreferenceRepository;
//import Holify.holify.repository.UserRepository;
//import com.google.firebase.auth.FirebaseAuth;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/onboarding")
//public class OnboardingController {
//    private final UserRepository userRepository;
//    private final SongRepository songRepository;
//    private final UserGenrePreferenceRepository genrePreferenceRepository;
//    private final UserArtistPreferenceRepository artistPreferenceRepository;
//    private final FirebaseAuth firebaseAuth;
//    private static final Logger log = LoggerFactory.getLogger(OnboardingController.class);
//
//    public OnboardingController(
//        UserRepository userRepository,
//        SongRepository songRepository,
//        UserGenrePreferenceRepository genrePreferenceRepository,
//        UserArtistPreferenceRepository artistPreferenceRepository,
//        FirebaseAuth firebaseAuth) {
//        this.userRepository = userRepository;
//        this.songRepository = songRepository;
//        this.genrePreferenceRepository = genrePreferenceRepository;
//        this.artistPreferenceRepository = artistPreferenceRepository;
//        this.firebaseAuth = firebaseAuth;
//    }
//
//    @GetMapping("/genres")
//    public ResponseEntity<List<String>> getGenres() {
//        List<String> genres = songRepository.findDistinctGenres();
//        log.info("Fetched {} genres", genres.size());
//        return ResponseEntity.ok(genres);
//    }
//
//    @PostMapping("/genres")
//    public ResponseEntity<String> saveGenres(
//        @RequestHeader("Authorization") String idToken,
//        @RequestBody List<String> genres) {
//        try {
//            String uidStr = firebaseAuth.verifyIdToken(idToken).getUid();
//            Long uid = Long.parseLong(uidStr);
//            if (!userRepository.existsById(uid)) {
//                log.error("User not found: {}", uid);
//                return ResponseEntity.badRequest().body("User not found");
//            }
//            if (genres.size() < 1 || genres.size() > 5) {
//                log.error("Invalid genre count: {}", genres.size());
//                return ResponseEntity.badRequest().body("Select 1–5 genres");
//            }
//            List<String> validGenres = songRepository.findDistinctGenres();
//            for (String genre : genres) {
//                if (!validGenres.contains(genre)) {
//                    log.error("Invalid genre: {}", genre);
//                    return ResponseEntity.badRequest().body("Invalid genre: " + genre);
//                }
//            }
//            genrePreferenceRepository.deleteByUserId(uid);
//            User user = userRepository.findById(uid).get();
//            for (String genre : genres) {
//                UserGenrePreference pref = new UserGenrePreference();
//                pref.setUser(user);
//                pref.setGenre(genre);
//                genrePreferenceRepository.save(pref);
//            }
//            log.info("User {} saved {} genres", uid, genres.size());
//            return ResponseEntity.ok("Genres saved");
//        } catch (Exception e) {
//            log.error("Error saving genres: {}", e.getMessage());
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/artists")
//    public ResponseEntity<List<String>> getArtists() {
//        List<String> artists = songRepository.findDistinctArtists();
//        log.info("Fetched {} artists", artists.size());
//        return ResponseEntity.ok(artists);
//    }
//
//    @PostMapping("/artists")
//    public ResponseEntity<String> saveArtists(
//        @RequestHeader("Authorization") String idToken,
//        @RequestBody List<String> artists) {
//        try {
//            String uidStr = firebaseAuth.verifyIdToken(idToken).getUid();
//            Long uid = Long.parseLong(uidStr);
//            if (!userRepository.existsById(uid)) {
//                log.error("User not found: {}", uid);
//                return ResponseEntity.badRequest().body("User not found");
//            }
//            if (artists.size() > 10) {
//                log.error("Too many artists: {}", artists.size());
//                return ResponseEntity.badRequest().body("Select up to 10 artists");
//            }
//            List<String> validArtists = songRepository.findDistinctArtists();
//            for (String artist : artists) {
//                if (!validArtists.contains(artist)) {
//                    log.error("Invalid artist: {}", artist);
//                    return ResponseEntity.badRequest().body("Invalid artist: " + artist);
//                }
//            }
//            artistPreferenceRepository.deleteByUserId(uid);
//            User user = userRepository.findById(uid).get();
//            for (String artist : artists) {
//                UserArtistPreference pref = new UserArtistPreference();
//                pref.setUser(user);
//                pref.setArtist(artist);
//                artistPreferenceRepository.save(pref);
//            }
//            log.info("User {} saved {} artists", uid, artists.size());
//            return ResponseEntity.ok("Artists saved");
//        } catch (Exception e) {
//            log.error("Error saving artists: {}", e.getMessage());
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }
//}