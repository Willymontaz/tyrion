package fr.pingtimeout.tyrion.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pingtimeout.tyrion.model.CriticalSectionEntered;
import fr.pingtimeout.tyrion.model.CriticalSectionEvent;

public class GUI {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CriticalSectionEntered event = mapper.readValue(
                "{\"enter\":{\"timestamp\":1372770101098,\"accessor\":{\"id\":1,\"name\":\"main\"},\"target\":{\"hashcode\":672184983,\"className\":\"HelloWorld\"}}}",
                CriticalSectionEntered.class);

        System.out.println(event);
    }
}