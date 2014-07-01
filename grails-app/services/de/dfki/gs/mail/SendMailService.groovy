package de.dfki.gs.mail

import de.dfki.gs.domain.Person
import de.dfki.gs.domain.PersonRole
import de.dfki.gs.domain.Role
import de.dfki.gs.domain.stats.PersistedCarAgentResult
import grails.transaction.Transactional

@Transactional
class SendMailService {

    def mailService


    def sendConfirmationMailToSigningUser( Person p, String confirmLink) {

        mailService.sendMail {
            to "${p.username}"
            from "emobilesim-team"
            subject "Your registration request"
            body "Please follow the link to complete your signing request: ${confirmLink}"
        }

    }

    def sendLoginAllowedMailToPerson( Person p ) {

        mailService.sendMail {
            to "${p.username}"
            from "emobilesim-team"
            subject "Your are now allowed to login to emobile"
            body "So please visit <emobile>"
        }

    }

    def sendConfirmationRequestToAdmins( Person p ) {

        Role adminRole = Role.findByAuthority( "ROLE_ADMIN" )

        List<PersonRole> adminRoles = PersonRole.findAllByRole( adminRole )

        adminRoles.each { PersonRole personRole ->

            Person adminPerson = Person.get( personRole.person.id )


            mailService.sendMail {
                to "${adminPerson.username}"
                from "emobilesim-team"
                subject "Confirmation process ${p.givenName} ${p.familyName} of company "
                body "${p.givenName} ${p.familyName} with email: ${p.username} tries to sign in as USER\n" +
                        " click here to check: +++\n" +
                        " click here to dismiss ---"
            }

        }

    }
}
