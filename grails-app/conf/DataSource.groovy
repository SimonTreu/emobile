dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    /*
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
    cache.provider_class='org.hibernate.cache.EhCacheProvider'
    */
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update" //"create-drop"
            // dbCreate = "create-drop"
            driverClassName = "com.mysql.jdbc.Driver"
            url = "jdbc:mysql://localhost/emobilesim"
            username = "emobilesim"
            password = "emobilesim"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }

    production {
        /*
        dataSource {
            dbCreate = "update"
            jndiName = "java:comp/env/jdbc/emobileDataSource"
        }
        */


        dataSource {
            dbCreate = "update" //"create-drop"
            driverClassName = "com.mysql.jdbc.Driver"
            url = "jdbc:mysql://localhost/emobilesim"
            username = "root"
            password = "reldb00"
            pooled = true
            properties = {
               maxActive = 500
               minEvictableIdleTimeMillis = 1800000
               timeBetweenEvictionRunsMillis = 1800000
               numTestsPerEvictionRun = 3
               testOnBorrow = true
               testWhileIdle = true
               testOnReturn = true
               validationQuery = "SELECT 1"
            }
        }

    }
}
