'use strict';

/* jasmine specs for controllers go here */

describe('Controllers', function(){

    beforeEach(function(){
        this.addMatchers({
            toEqualData: function(expected) {
                return angular.equals(this.actual, expected);
            }
        });
    });

    beforeEach(module('virkailija.services'));
    beforeEach(module('virkailija'));

    describe('SearchCtrl', function(){
        var scope, ctrl, $httpBackend;

        beforeEach(inject(function(_$httpBackend_, $rootScope, $controller, Config) {

            $httpBackend = _$httpBackend_;
            $httpBackend.expectGET("/context.html/hakemukset?term=1.2.3.4.5.0").
                respond([{answers:{henkilotiedot:{Sukunimi: "Testaaja", Etunimet: "Teppo Topias", Henkilotunnus: "120187-1234"}}, oid : "1.2.3.4.5.1", state: "Voimassa"}]);
            scope = $rootScope.$new();
            ctrl = $controller(SearchCtrl, {$scope: scope, Config: Config});
        }));


        it('should search "applications" model with 2 application fetched', function() {
            expect(scope.applications).toEqual([]);
            scope.term = "1.2.3.4.5.0";
            scope.search();
            $httpBackend.flush();
            expect(scope.applications.length).toBe(1);
            expect(scope.applications).toEqualData([{answers:{henkilotiedot:{Sukunimi: "Testaaja", Etunimet: "Teppo Topias", Henkilotunnus: "120187-1234"}}, oid : "1.2.3.4.5.1", state: "Voimassa"}]);
        });


        it('should clear all the fetched applications when reset called', function() {
            scope.term = "1.2.3.4.5.0";
            scope.search();
            $httpBackend.flush();
            scope.reset();
            expect(scope.applications).toEqual([]);
        });
    });
});


describe('MyCtrl2', function(){
  var myCtrl2;


  beforeEach(function(){
    //myCtrl2 = new MyCtrl2();
  });


  it('should ....', function() {
    //spec body
  });
});
