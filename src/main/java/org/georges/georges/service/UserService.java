package org.georges.georges.service;

import org.georges.georges.pojos.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Hashtable;


@Service
public class UserService {
    // creation dictionnaire user for local uses
    Hashtable<Integer, User> membresDict = new Hashtable<Integer, User>();

    public UserService() {

        //creation of member
        User user = new User("maxime", "moxi", "maxime.moxi@ipilyon.net", "12345", new Date("2019/11/13"));
        user.setId(1);
        //add member on dict
        membresDict.put(1, user);
    }

    // Récupère un membre à partir de son id
    public User getMembreFromId(int id) {

        return membresDict.get(id);
    }

    public User ajoutMembre(User user) {

        // génère l'id du membre
        user.setId(membresDict.size() + 1);

        // ajoute le membre dans le dictionnaire
        membresDict.put(user.getId(), user);

        return user;
    }
}
