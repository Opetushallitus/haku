<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  --%>

<head>

    <title>Opetushallitus</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/haku/resources/css/screen.css" type="text/css" rel="stylesheet"/>
    <link href="/haku/resources/jquery/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css" rel="stylesheet"/>
    <link href='http://fonts.googleapis.com/css?family=PT+Sans:400,700,400italic,700italic' type='text/css'
          rel='stylesheet'/>
    <script src="/haku/resources/jquery/jquery-1.8.0.min.js" type="text/javascript"></script>
    <script src="/haku/resources/jquery/jquery-ui-1.8.23.custom.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="/haku/resources/javascript/master.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            $(".suodatin").click(function() {
                $('#hakusuodattimet').submit();
            });
        })
    </script>

</head>
