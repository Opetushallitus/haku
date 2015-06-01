function() {
    var BreakException= {};
    
    cursor = db.application.find({"answers.koulutustausta": {$exists:true}});
            
    i = 0;
    onkoVirkailijaMuutos = function(history) {
        return history.modifier.match(/1\.2\.246\.562\.24.*/);
    }
    ensimmainenMuutos = function(history) {
        current = history[0];
        history.forEach(function(h) {
          if(h.modified > current.modified) {
              current = h;
          }
        });
        return current;
    }
    ainoastaanVirkailijaMuutokset = function(history) {
        return history.filter(function(h){
            return onkoVirkailijaMuutos(h);
        })
    }
    pk_map = {};
    onkoLahtoluokkaTaiLuokkataso = function(property) {
        if("lahtoluokka" === property) {
            return true;
        } else if("luokkataso" === property) {
            return true;
        } else if("lahtokoulu-parents" === property) {
            return true;
        } else if("lahtokoulu" === property) {
            return true;
        } else if("vaiheId" === property) {
            return true;
        } else if(property.match(/pohjakoulutus_.*/)) {
            if(pk_map[property]) {
            } else {
                pk_map[property] = true;
                printjson(property);
            }
            return true;
        } else if(property.match(/preference.-amkLiite/)) {
            return true;
        } else {
            return false;
        }
    }
    muutoksetHistorysta = function(property, history) {
        if(history) {
            return history.filter(function(h){
                return h.changes.filter(function(c){return c.field === property}).length != 0;
            }).map(function(h){
                return {
                    modified: h.modified,
                    modifier: h.modifier,
                    changes: h.changes.filter(function(c){return c.field === property})
                };
            });
        } else {
            return [];
        }
    }
    muutosToPaluuarvo = function(ekamuutos) {
        if(onkoVirkailijaMuutos(ekamuutos)) {
            if("new value" in ekamuutos.changes[0]) {
                return ekamuutos.changes[0]["new value"];
            } else {
                return "(null)";
            }
        } else if("old value" in ekamuutos.changes[0]) {
            return ekamuutos.changes[0]["old value"];
        } else {
            return "(null)";
        }
    }
    
    cursor.forEach(function(hakemus) {
        muutoksia = [];
        virkailijamuutoksia = false;
        for (var property in hakemus.answers.koulutustausta) {
            if(onkoLahtoluokkaTaiLuokkataso(property)) {
                // ohitetaan lahtoluokka ja luokkataso
                continue;
            }
            muutoksetHistoriassa = muutoksetHistorysta(property, hakemus.history);
            pelkatVirkailijaMuutokset = ainoastaanVirkailijaMuutokset(muutoksetHistoriassa);
            onkoHistoriaMuutoksia = muutoksetHistoriassa.length != 0;
            onkoHistoriassaVirkailijaMuutoksia = pelkatVirkailijaMuutokset.length != 0;
            onkoOverridenMuutoksia = false;
            if(hakemus.overriddenAnswers && property in hakemus.overriddenAnswers) {
                onkoOverridenMuutoksia = true;
            }
            onkoYlipaataanMuutoksia = onkoOverridenMuutoksia || onkoHistoriaMuutoksia;
            if(onkoYlipaataanMuutoksia) {
                tilanne = {
                    hakemusOid: hakemus.oid,
                    avain: property,
                    arvo: hakemus.answers.koulutustausta[property]
                }
                if(onkoHistoriassaVirkailijaMuutoksia) {
                    pelkatVirkailijaMuutokset
                }
                if(onkoHistoriassaVirkailijaMuutoksia) { 
                     // Vain virkailijamuutokset halutaan tallentaa
                    uusinVirkailijaMuutos = ensimmainenMuutos(pelkatVirkailijaMuutokset);
                    tilanne["palautettava_arvo"]=
                        muutosToPaluuarvo(ensimmainenMuutos(pelkatVirkailijaMuutokset));
                    tilanne["palautettava_arvo_lahde"] = "Virkailijan " + uusinVirkailijaMuutos.modifier + " muutos arvoon "+tilanne["palautettava_arvo"]+" (korvattu arvo oli "+tilanne.arvo+") palautettu koulutustaustan muuttujalle " + property;
                    if(tilanne["arvo"] !== tilanne["palautettava_arvo"]) {
                                            virkailijamuutoksia = true;
                    }
                } else if(onkoOverridenMuutoksia) {
                    // Ei virkailijamuutoksia niin overriden arvo palautetaan
                    tilanne["palautettava_arvo"]=hakemus.overriddenAnswers[property];
                    tilanne["palautettava_arvo_lahde"] = "Ylikirjoitettu arvo " + tilanne["palautettava_arvo"] + " (korvattu arvo oli "+tilanne.arvo+") palautettu muuttujalle " + property;
                } else {
                    // ei overriden eika virkailijamuutoksia
                    printjson("POIKKEUS");
                    printjson(muutoksetHistoriassa);
                    printjson(hakemus);
                    throw BreakException;
                }
                if(tilanne["arvo"] === tilanne["palautettava_arvo"]) {
                    // Ei muutoksia koska palautettava arvo vastaa nykyista arvoa
                } else {
                    muutoksia.push(tilanne);
                    if(!hakemus.notes) {
                        hakemus.notes = [];
                    }
                    hakemus.notes.push({
                        type: "ApplicationNote",
                        noteText: tilanne["palautettava_arvo_lahde"],
                        added: NumberLong(new Date().getTime()),
                        user: "Vanhojen koulutustietojen palautus skripti"
                    });
                    if("(null)" === tilanne["palautettava_arvo"]) {
                        delete hakemus.answers.koulutustausta[property];
                    } else {
                        hakemus.answers.koulutustausta[property] = tilanne["palautettava_arvo"];
                    }
                }
            }
        }
        if(muutoksia.length === 0) {
        } else {
            i = i + 1; // muutosindeksin kasvatus
            if(virkailijamuutoksia) {
                db.application.save(hakemus);
            }
            if(!virkailijamuutoksia) {
                db.application.save(hakemus);
            }
        }
        if(i == 100000) { // sivun koko
            // muutosten sivutusta varten
            //throw BreakException;
        }
    });
    
}

