'use strict';

/* Services */
  
angular.module('hakemusServices', ['ngResource']).
    factory('Hakemus', function($resource){
  return $resource('/haku/hakemus/:oid', {oid:'@oid'},
   {'get':    {method:'GET'}});
});


