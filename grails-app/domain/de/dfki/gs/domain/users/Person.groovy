package de.dfki.gs.domain.users

class Person {

    transient springSecurityService

    String username

    String givenName
    String familyName

    String confirmationCode

    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    Company company

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true
        password blank: false

        givenName blank: false
        familyName blank: false

        confirmationCode blank: false

    }

    static mapping = {
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        PersonRole.findAllByPerson(this).collect { it.role } as Set
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService.encodePassword(password)
    }
}