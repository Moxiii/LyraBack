package org.georges.georges.Projets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjetService {
    @Autowired
    ProjetsRepository projetsRepository;
}
