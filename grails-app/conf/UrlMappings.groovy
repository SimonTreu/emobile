class UrlMappings {

	static mappings = {

        "/" {
            controller = "login"
            action = "index"
        }

        "/logout" {
            controller = "login"
            action = "logout"
        }

        "/sim" {
            controller = "front"
            action = "init"
        }

        "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }


        "/map/googleMaps" {
            controller = "mapView"
            action = "googleMaps"
        }

        "/map/openLayers" {
            controller = "mapView"
            action = "openLayers"
        }


        "/map/openLayersWithAction" {
            controller = "mapView"
            action = "openLayersWithAction"
        }

        "/openLayersWithAction" {
            controller = "mapView"
            action = "openLayersWithAction"
        }

        "/maps/openLayersMapsStatistik" {
            controller = "mapView"
            action = "openLayersMapsStatistik"
        }

        "/openLayersMapsStatistik" {
            controller = "mapView"
            action = "openLayersMapsStatistik"
        }

        "/showPlaySimulation" {
            controller = "mapView"
            action = "showPlaySimulation"
        }

        "/startSimulation" {
            controller = "simulation"
            action = "startSimulation"
        }

        "/import" {
            controller = "fileUpload"
            view = "index"
        }

        "/simulation" {
            controller = "simulation"
            action = "init"
        }


        /*
        "/" {
            controller = "front"
            action = "init"
        }
        */


        "500"(view:'/error')
	}
}
