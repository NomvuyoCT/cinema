"use strict";
import {byId, toon, verberg, verwijderChildElementen} from "./util.js";
byId("zoek").onclick = async function (){
    verbergReservatieEnFouten();
    const emailAdres = byId("emailAdres");
    if (emailAdres.checkValidity()){
        findByEmailAdres(emailAdres.value);
    }
    else {
        toon("emailFout")
        emailAdres.focus();
        return;
    }
}
function verbergReservatieEnFouten(){
    verberg("emailFout");
    verberg("storing");
    verberg("reservatiesTable");
}
async function  findByEmailAdres(emailAdres){
    const response = await fetch(`reservaties?emailAdres=${emailAdres}`);
    if (response.ok) {
        const reservaties = await response.json();
        toon("reservatiesTable");
        const reservatiesBody = byId("reservatiesBody");
        verwijderChildElementen(reservatiesBody);
        for (const reservatie of reservaties){
            const tr = reservatiesBody.insertRow();
            tr.insertCell().innerText = reservatie.id;
            tr.insertCell().innerText = reservatie.titel;
            tr.insertCell().innerText = reservatie.plaatsen;
            tr.insertCell().innerText =
                new Date(reservatie.besteld).toLocaleString("nl-BE");
        }
    }
    else toon("storing");
}