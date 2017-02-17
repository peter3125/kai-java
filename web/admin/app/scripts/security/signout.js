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

'use strict';

/**
 * @ngdoc function
 * @name webApp.controller:SignOutController
 * @description
 * # SignOutController
 * Controller of the webApp
 */
angular.module('webApp')
    .controller('SignOutController', function ($scope, globalSvc, $location) {

        // wipe session
        globalSvc.setSession(null);
        globalSvc.goHome();

    });
