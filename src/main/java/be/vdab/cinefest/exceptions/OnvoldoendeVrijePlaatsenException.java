package be.vdab.cinefest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OnvoldoendeVrijePlaatsenException extends RuntimeException{
    private final static long serialVersionUID = 1L;
    public OnvoldoendeVrijePlaatsenException(){
        super("Niet genoeg vrije plaatsen");
    }
}
