/*
 * Copyright (c) 2016 by Peter de Vocht
 *
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 *
 */

package industries.vocht.viki.semantic_search;

import industries.vocht.viki.dao.PennType;
import industries.vocht.viki.model.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by peter on 8/06/16.
 *
 * give any very, get its proto-type index verb and penn-type
 *
 */
@Component
public class VerbsProtoTypes {

    // folder / path to the lexicon files
    @Value("${lexicon.base.directory:/opt/kai/data/lexicon}")
    private String lexiconBaseDirectory;

    @Value("${verbs.proto.type.list.filename:verb-types.txt}")
    private String protoTypeFilename;

    @Value("${verbs.conjugation.filename:verbs.txt}")
    private String conjugationFilename;

    private HashMap<String, List<Token>> conjugationSet;
    private HashMap<String, HashSet<String>> verbToProtoVerb;

    public VerbsProtoTypes() {
    }

    /**
     * given a verb (any kind of verb), return its proto-type verb list
     * @param verb the verb to check (any conjugation)
     * @return if we have a proto-type for it, the list of proto-types for this verb
     */
    public HashSet<String> getVerbProtoType(String verb ) {
        if( verb != null ) {
            String baseVerb = getBase(verb);
            if ( baseVerb != null ) {
                return verbToProtoVerb.get(baseVerb);
            }
        }
        return null;
    }

    // spring init
    public void init() throws IOException {
        loadVerbConjugations();
        loadProtoVerbs();
    }

    /**
     * get the base conjugation verb of the verb
     * @param verb the verb to check
     * @return null if dne, otherwise the VB base verb
     */
    private String getBase( String verb ) {
        List<Token> list = conjugationSet.get(verb.toLowerCase().trim());
        if ( list != null ) {
            for ( Token token : list ) {
                if ( token.getPennType() == PennType.VB ) {
                    return token.getText();
                }
            }
        }
        return null;
    }

    /**
     * process the proto-type verb file and put it in the system maps
     * @throws IOException
     */
    private void loadProtoVerbs() throws IOException {
        verbToProtoVerb = new HashMap<>();
        List<String> lineList = Files.readAllLines(Paths.get( lexiconBaseDirectory + File.separator + protoTypeFilename ) );
        for ( String line : lineList ) {
            String[] items = line.split("\\|");
            if ( items.length == 2 ) {
                String protoVerb = items[0].replace('_', ' ').trim();
                addToProtoVerbMap(protoVerb, protoVerb);
                String[] verbList = items[1].split(",");
                for ( String verb : verbList ) {
                    addToProtoVerbMap(protoVerb, verb.replace('_', ' '));
                }
            }
        }
    }

    /**
     * helper routine, add verb -> list<proto-verb> mapping
     * @param protoVerb the proto-verb
     * @param verb the verb to map to a proto-verb
     */
    private void addToProtoVerbMap(String protoVerb, String verb) {
        if ( verbToProtoVerb.containsKey(verb) ) {
            verbToProtoVerb.get(verb).add(protoVerb);
        } else {
            HashSet<String> list = new HashSet<>();
            list.add(protoVerb);
            verbToProtoVerb.put(verb, list);
        }
    }


    /**
     * load and setup the verb conjugation system
     * @throws IOException
     */
    private void loadVerbConjugations() throws IOException {
        // load the plural set generated by specialist lexicon
        conjugationSet = new HashMap<>();
        List<String> lineList = Files.readAllLines(Paths.get( lexiconBaseDirectory + File.separator + conjugationFilename ) );
        for ( String line : lineList ) {
            String[] items = line.split("\\|");
            if ( items.length >= 6 ) {
                List<Token> tokenList = new ArrayList<>();
                for ( String item : items ) {
                    String wordStr = getWord(item);
                    Token token = new Token(wordStr, getTag(item));
                    tokenList.add(token);
                }
                for ( Token token : tokenList ) {
                    addConjugation( token.getText(), tokenList );
                }

            }
        }
    }

    /**
     * process a single conjugation item, add list to all existing conjugations for str
     * @param str the verb instance
     * @param tokenList the list of related items to add
     */
    private void addConjugation( String str, List<Token> tokenList ) {
        if (conjugationSet.containsKey(str)) {
            List<Token> existingList = conjugationSet.get(str);
            for ( Token token : tokenList ) {
                if ( !contains(existingList, token) ) {
                    existingList.add(token);
                }
            }
        } else {
            conjugationSet.put(str, tokenList);
        }

    }

    /**
     * return true if tokenList contains token
     * @param tokenList the list to check
     * @param token the token in the list?
     * @return true if the token is in the list
     */
    private boolean contains( List<Token> tokenList, Token token ) {
        if ( tokenList != null && token != null ) {
            for ( Token token2 : tokenList ) {
                if ( token2.getText().compareToIgnoreCase(token.getText()) == 0 &&
                     token2.getPennType() == token.getPennType() ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * split string off str:tag
     * @param str the string to split
     * @return the word part of the string
     */
    private String getWord( String str ) {
        if ( str != null && str.contains(":") ) {
            return str.split(":")[0];
        }
        return str;
    }

    /**
     * split off the penn type from a string
     * @param str the string
     * @return the penn type
     * @throws IOException
     */
    private PennType getTag(String str ) throws IOException {
        if ( str != null && str.contains(":") ) {
            return PennType.fromString(str.split(":")[1]);
        }
        throw new IOException("getTag() must contain : (" + str + ")");
    }



}


