package com.UPBEATg11.OOP_Project;

import entities.CityCrew;
import entities.Territory;
import orchestrator.IllegalConfiguration;
import orchestrator.MissingConfigurationVariable;
import orchestrator.Upbeat;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import parsers.SyntaxError;

import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class JoiningController {
    @MessageMapping("/updateUsers")
    @SendTo("/topic/joinedUsers")
    public Object getUsers(){
        return Upbeat.crews;
    }

    @MessageMapping("/join")
    @SendToUser("/queue/token")
    public Object pushUser(@Payload String username, Principal user){
        if(Upbeat.getGameState() == Upbeat.GameState.configSetting)
            Upbeat.gameState = Upbeat.GameState.joining;

        List<CityCrew> crews = Upbeat.crews;
        String uuid = UUID.randomUUID().toString();
        int crewSize = crews.size();
        try {
            Map<String, Long> config = Upbeat.getConfig();
            if(Upbeat.game == null)
                Upbeat.game = new Territory(config);

            crews.add(Upbeat.randomizedInitCrew(username, crewSize, uuid));
        } catch (SyntaxError | FileNotFoundException | MissingConfigurationVariable | RuntimeException |
                 IllegalConfiguration e)
        {/*It just cannot create an error*/ System.out.println("Whoops");}

        return Map.of("token",uuid,"crewId",crewSize);
    }
}
