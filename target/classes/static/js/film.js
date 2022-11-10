"use strict";
import {byId, toon, setText, verberg} from "./util.js";
byId("zoek").onclick = async function (){
    verbergFilmEnFouten();
    const zoekIdInput = byId("zoekId");
    if (! zoekIdInput.checkValidity()){
        toon("zoekIdFout");
        zoekIdInput.focus();
        return;
    }
    findById(zoekIdInput.value);
};

byId("verwijder").onclick = async function(){
    const zoekIdInput = byId("zoekId");
    const response = await fetch(`films/${zoekIdInput.value}`, {method: "DELETE"});
    if (response.ok){
        verbergFilmEnFouten();
        zoekIdInput.value = "";
        zoekIdInput.focus();
    } else {
        toon("storing");
    }
};
byId("bewaar").onclick = async function () {
    const nieuweTitelInput = byId("nieuweTitel");
    if(nieuweTitelInput.checkValidity()){
        verberg("nieuweTitelFout");
        updateFilm(nieuweTitelInput.value);
    } else {
        toon("nieuweTitelFout");
        nieuweTitelInput.focus()
    }

};
byId("reserveer").onclick = async function () {
    verberg("emailAdresFout");
    verberg("plaatsenFout");
    const emailInput = byId("emailAdres");
    if (! emailInput.checkValidity()){
        toon("emailAdresFout");
        emailInput.focus();
        return;
    }
    const plaatsenInput = byId("plaatsen");
    if (! plaatsenInput.checkValidity()){
        toon("plaatsenFout");
        plaatsenInput.focus();
        return;
    }
    const toevoegReservatie = {
        emailAdres : emailInput.value,
        plaatsen: plaatsenInput.value
    };
    reserveerFilm(toevoegReservatie)
}
function verbergFilmEnFouten(){
    verberg("film");
    verberg("zoekIdFout");
    verberg("nietGevonden");
    verberg("nieuweTitelFout")
    verberg("emailAdresFout");
    verberg("plaatsenFout");
    verberg("storing");
    verberg("conflict");
}
async function findById(id){
    const response = await fetch(`films/${id}`)
    if (response.ok){
        const film = await response.json();
        toon("film")
        setText("titel", film.titel)
        setText("jaar", film.jaar);
        setText("vrijePlaatsen", film.vrijePlaatsen);
    } else{
        if (response.status === 404){
            toon("nietGevonden");
        } else {
            toon("storing");
        }
    }
}
async function updateFilm(titel){
    const response  = await fetch(`films/${byId("zoekId").value}/titel`,
        {
            method: "PATCH",
            headers: {'Content-Type': "application/json"},
            body: JSON.stringify(titel)
        });
    if (response.ok){
        setText("titel", titel);
    } else {
        toon("storing")
    }
}
async function reserveerFilm(toevoegReservatie){
    verberg("nietGevonden");
    verberg("storing");
    verberg("conflict");
    const response = await fetch(`films/${byId("zoekId").value}/reservaties`,
    {
        method: "POST",
        headers: {'Content-Type': "application/json"},
        body: JSON.stringify(toevoegReservatie)
    });
    if (response.ok){
        window.location = "allefilms.html";
    } else {
        switch (response.status){
            case 404:
                toon("nietGevonden");
                break;
            case 409:
                const responseBody = await response.json();
                setText("conflict", responseBody.message);
                toon("conflict");
            default:
                toon("storing");
        }
    }
}
