package hu.lk.card_reader;


import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
public class CardReaderApplication {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CardReaderApplication.class);

    @RestController
    class CardReaderController {

        private final JdbcTemplate jdbcTemplate;

        public CardReaderController(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @GetMapping("/read")
        ReadCardResponse readCard(@RequestParam(value = "cardId", required = true) String cardId,
                                  @RequestParam(value = "cardReaderId", required = true) String cardReaderId) {

            ReadCardResponse readCardResponse = new ReadCardResponse();

            Map<String, Object> studentProperties = this.jdbcTemplate.queryForList("SELECT name, student_id FROM student WHERE card_id = '" + cardId + "'")
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("No student is registered with cardId %s", cardId)));

            String studentName = (String) studentProperties.get("name");
            String studentId = (String) studentProperties.get("student_id");
            readCardResponse.setStudentName(studentName);

            String classroom = getClassRoomByReaderId(cardReaderId);

            String time = getTime();

            Optional<Map<String, Object>> classProperties = this.jdbcTemplate.queryForList("SELECT class_id, class_name FROM class WHERE from_time <= '" + time + "' AND to_time > '" + time + "' AND classroom = '" + classroom + "'")
                    .stream()
                    .findFirst();

            if (classProperties.isPresent()) {
                readCardResponse.setClassName((String) classProperties.get().get("class_name"));

                String classId = (String) classProperties.get().get("class_id");
                Long count = this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM student_class_registration WHERE class_id = '" + classId + "' AND student_id = '" + studentId + "'", Long.class);

                readCardResponse.setInClass(count > 0);
                if (count > 0) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    String date = localDateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);


                    this.jdbcTemplate.execute(String.format("REPLACE INTO  card_reader_db.student_attendence VALUES ('%s', '%s', '%s')", studentId, classId, date));


                }
            }

            return readCardResponse;
        }

        @GetMapping("/schedule")
        String schedule(@RequestParam(value = "cardReaderId", required = true) String cardReaderId) {

            String classroom = getClassRoomByReaderId(cardReaderId);

            String time = getTime();

            String sql = "SELECT class_name FROM class WHERE from_time <= '" + time + "' AND to_time > '" + time + "' AND classroom = '" + classroom + "'";
            log.info("Executing query: {}", sql);
            Optional<String> className = this.jdbcTemplate.queryForList(sql, String.class)
                    .stream()
                    .findFirst();

            return className.orElse("NO CLASS");
        }

        private String getClassRoomByReaderId(@RequestParam(value = "cardReaderId", required = true) String cardReaderId) {
            return this.jdbcTemplate.queryForList("SELECT classroom FROM card_reader WHERE card_reader_id = '" + cardReaderId + "'", String.class)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("No card reader is registered with cardReaderId %s", cardReaderId)));
        }
    }

    private static String getTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public static void main(String[] args) {
        SpringApplication.run(CardReaderApplication.class, args);
    }

}
