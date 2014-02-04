package de.dfki.gs.service.simulation

import grails.async.DelegateAsync
import grails.async.Promise
import grails.transaction.Transactional

@Transactional
class AsyncSimulationFrameworkService {

    @DelegateAsync SimulationFrameworkService simulationFrameworkService

    /*
    Promise init( long  ) {
        Promises.task {
            bookService.findBooks(title)
        }
    }
    */
}
