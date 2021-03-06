//
// Copyright Red Hat, Inc.
//
// SPDX-License-Identifier: GPL-2.0-or-later
//
package org.dogtagpki.acme.validator;

import java.security.MessageDigest;
import java.util.Hashtable;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.dogtagpki.acme.ACMEAuthorization;
import org.dogtagpki.acme.ACMEChallenge;
import org.dogtagpki.acme.ACMEError;
import org.dogtagpki.acme.ACMEIdentifier;

/**
 * @author Endi S. Dewata
 */
public class DNS01Validator extends ACMEValidator {

    public static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DNS01Validator.class);

    public DNS01Validator() {
        super("DNS-01", "dns-01");
    }

    public String generateKeyAuthorization(String accountID, ACMEChallenge challenge) throws Exception {

        // DNS-01 key authorization
        // https://tools.ietf.org/html/rfc8555
        // TODO: make it configurable

        String keyAuthorization = challenge.getToken() + "." + accountID;
        MessageDigest digest = MessageDigest.getInstance("SHA-256", "Mozilla-JSS");
        byte[] hash = digest.digest(keyAuthorization.getBytes("UTF-8"));
        return Base64.encodeBase64URLSafeString(hash);
    }

    public void validateChallenge(
            ACMEAuthorization authorization,
            ACMEChallenge challenge) throws Exception {

        String accountID = authorization.getAccountID();
        String keyAuthorization = generateKeyAuthorization(accountID, challenge);
        logger.info("Key authorization: " + keyAuthorization);

        ACMEIdentifier identifier = authorization.getIdentifier();
        String hostname = identifier.getValue();
        String recordName = "_acme-challenge." + hostname;

        // TODO: move retry to ACMEChallengeProcessor.processChallenge()
        // TODO: make it configurable

        int maxCount = 5;
        int interval = 5;

        int count = 1;
        String response = null;
        Exception exception = null;

        while (true) {
            try {
                response = getResponse(recordName);
                break;

            } catch (Exception e) {
                // TODO: catch more specific DNS exception

                logger.error(e.getMessage());
                if (count >= maxCount) {
                    exception = e;
                    break;
                }

                Thread.sleep(interval * 1000);
                count++;
            }
        }

        if (exception != null) {

            logger.error("Unable to validate DNS-01 challenge: " + exception.getMessage(), exception);

            ACMEError error = new ACMEError();
            error.setType("urn:ietf:params:acme:error:dns");
            error.setDetail(
                    "Unable to validate DNS-01 challenge at " + recordName + "\n" +
                    "Error: " + exception.getMessage());

            throwError(Response.Status.BAD_REQUEST, error);
        }

        if (response == null || !response.equals(keyAuthorization)) {

            logger.error("Invalid response: " + response);

            ACMEError error = new ACMEError();
            error.setType("urn:ietf:params:acme:error:incorrectResponse");
            error.setDetail(
                    "Unable to validate DNS-01 challenge at " + recordName + "\n" +
                    "Incorrect response: " + response);

            throwError(Response.Status.BAD_REQUEST, error);
        }
    }

    public String getResponse(String recordName) throws Exception {

        logger.info("Retrieving TXT record: " + recordName);

        // TODO: remove dependency on Sun's library

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

        DirContext dirContext = new InitialDirContext(env);
        Attributes attrs = dirContext.getAttributes(recordName, new String[] { "TXT" });

        Attribute attr = attrs.get("TXT");
        String response = (String) attr.get();
        logger.info("Response: " + response);

        return response;
    }
}
