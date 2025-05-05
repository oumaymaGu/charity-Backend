package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tn.example.charity.Entity.NotificationRefuge;
import tn.example.charity.Service.NotificationRefugeService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/notificationrefuge")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationRefugeController {

    @Autowired
    private NotificationRefugeService notificationRefugeService;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/stream")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    @PostMapping("/send")
    public void send(@RequestBody String message) {
        NotificationRefuge saved = notificationRefugeService.save(message);
        sendToClients(saved);
    }

    private void sendToClients(NotificationRefuge notification) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notificationrefuge")
                        .data(notification));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }

    @GetMapping
    public List<NotificationRefuge> getAll() {
        return notificationRefugeService.getAll();
    }

    @PostMapping("/mark-as-read/{id}")
    public void markAsRead(@PathVariable Long id) {
        notificationRefugeService.markAsRead(id);
    }
}