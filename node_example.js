/*
Node.js script to test haku-app streaming API.
Prereqs: 
npm install --save cheerio bluebird request JSONStream event-stream

Create file with three lines:
my-username
my-password
https://host

Start streaming with command:
node node_example.js file_with_username_password_and_host.txt

*/
const http = require('http')
const fs = require('fs')
const request = require('request')
const cheerio = require('cheerio')
const Promise = require('bluebird')
const JSONStream = require('JSONStream')
const es = require('event-stream')

const [file_with_creds_and_host] = process.argv.slice(-1)
const [username, password, hostname] = fs.readFileSync(file_with_creds_and_host, 'utf8').split(/\r?\n/)

const postForm = (url, form) => {
	return new Promise((resolve, reject) => {
		request.post(url, {form:form}, (error, response, body) => {
			if(error) {
				reject(error)
			} else {
				resolve(body)
			}
		})
	})
}

const ticketGrantingTicketFromResponseHtml = (htmlPageWithTicket) => {
	$ = cheerio.load(htmlPageWithTicket)
	const tgt = $('form').attr('action')
	return tgt
}

postForm(hostname + '/cas/v1/tickets', {username:username, password:password})
	.then(ticketGrantingTicketFromResponseHtml)
	.then((tgtUrl) => postForm(tgtUrl,{service:hostname+'/haku-app/j_spring_cas_security_check'}))
	.then((serviceTicket) => {
		const path = '/haku-app/streaming/applications/listfull'
		const url = hostname + path
                console.log('Calling URL ' + url)
		request({	url: url,
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					"CasSecurityTicket": serviceTicket
				},
				json: {
					"searchTerms":"",
					"asIds":["1.2.246.562.29.25191045126"],
					"aoOids":[],
					"states":["ACTIVE","INCOMPLETE"],
				}
			}).pipe(JSONStream.parse('*'))
			.pipe(es.mapSync(function (data) {
			    console.error('Got one! ' + data.oid)
			    return 1
			  }))
	})
