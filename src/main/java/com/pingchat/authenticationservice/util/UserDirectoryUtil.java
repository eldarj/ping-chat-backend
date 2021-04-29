package com.pingchat.authenticationservice.util;

import com.jamesmurty.utils.XMLBuilder;
import com.pingchat.authenticationservice.config.FreeswitchConfiguration;
import com.pingchat.authenticationservice.model.dto.FsUser;
import org.springframework.stereotype.Component;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.Map;

@Component
public class UserDirectoryUtil {
    private static final Map<String, String> DEFAULT_USER_PARAMS = Map.of("password", "1234");
    private static final Map<String, String> DEFAULT_USER_VARIABLES = Map.of(
            "toll_allow", "domestic,international,local",
            "user_context", "default",
            "outbound_caller_id_name", "$${outbound_caller_name}",
            "outbound_caller_id_number", "$${outbound_caller_id}"
    );

    private final FreeswitchConfiguration freeswitchConfiguration;

    public UserDirectoryUtil(FreeswitchConfiguration freeswitchConfiguration) {
        this.freeswitchConfiguration = freeswitchConfiguration;
    }

    public String buildXmlOfPhoneNumbers(List<String> phoneNumbers)
            throws ParserConfigurationException, TransformerException {
        XMLBuilder usersListNode = buildUsersListNode();

        phoneNumbers.forEach(p -> {
            XMLBuilder userNode = usersListNode.element("user")
                    .attr("id", p);

            XMLBuilder paramsNode = userNode.element("params");
            DEFAULT_USER_PARAMS.forEach((key, value) -> {
                paramsNode.element("param")
                        .attr("name", key)
                        .attr("value", value);
            });

            XMLBuilder variablesNode = userNode.element("variables");
            DEFAULT_USER_VARIABLES.forEach((key, value) -> {
                variablesNode.element("variable")
                        .attr("name", key)
                        .attr("value", value);
            });

            variablesNode.element("variable")
                    .attr("name", "accountcode")
                    .attr("value", p);

            variablesNode.element("variable")
                    .attr("name", "effective_caller_id_name")
                    .attr("value", "Extension" + p);

            variablesNode.element("variable")
                    .attr("name", "effective_caller_id_number")
                    .attr("value", p);
        });

        return usersListNode.asString();
    }

    private XMLBuilder buildUsersListNode() throws ParserConfigurationException {
        return XMLBuilder.create("document")
                .attribute("type", "freeswitch/xml")
                .element("section")
                .attribute("name", "directory")
                .element("domain")
                .attribute("name", freeswitchConfiguration.getHost())
                .element("params")
                .element("param")
                .attribute("name", "dial-string")
                .attribute("value", "{^^:sip_invite_domain=${dialed_domain}:presence_id=${dialed_user}@${dialed_domain}}${sofia_contact(*/${dialed_user}@${dialed_domain})},${verto_contact(${dialed_user}@${dialed_domain})}")
                .up()
                .comment("These are required for Verto to function properly")
                .element("param")
                .attribute("name", "jsonrpc-allowed-methods")
                .attribute("value", "verto")
                .up()
                .comment("<param name=\"jsonrpc-allowed-event-channels\" value=\"demo,conference,presence\"/>")
                .up()
                .element("users");
    }
}
