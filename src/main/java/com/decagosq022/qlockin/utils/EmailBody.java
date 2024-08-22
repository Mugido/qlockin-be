package com.decagosq022.qlockin.utils;

public class EmailBody {

        public static String buildEmail(String fullName, String link) {

            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Email Verification</title>\n" +
                    "    <style>\n" +
                    "        /* Reset CSS */\n" +
                    "        body, h1, p {\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            line-height: 1.6;\n" +
                    "        }\n" +
                    "\n" +
                    "        /* Email wrapper */\n" +
                    "        .email-wrapper {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 0 auto;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        /* Header */\n" +
                    "        .header {\n" +
                    "            background-color: #6A0DAD;\n" +
                    "            color: white;\n" +
                    "            text-align: center;\n" +
                    "            padding: 20px 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        /* Content */\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "            border-radius: 8px;\n" +
                    "        }\n" +
                    "\n" +
                    "        /* Button */\n" +
                    "        .btn {\n" +
                    "            display: inline-block;\n" +
                    "            background-color: #d0dbe7;\n" +
                    "            color: black;\n" +
                    "            text-decoration: none;\n" +
                    "            padding: 10px 20px;\n" +
                    "            border-radius: 5px;\n" +
                    "            margin-top: 20px;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        /* Footer */\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            margin-top: 20px;\n" +
                    "            font-size: 12px;\n" +
                    "            color: #666;\n" +
                    "        }\n" +
                    "\n" +
                    "        /* Media Queries */\n" +
                    "        @media screen and (max-width: 600px) {\n" +
                    "            .email-wrapper {\n" +
                    "                width: 100%;\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div class=\"email-wrapper\">\n" +
                    "    <!-- Header -->\n" +
                    "    <div class=\"header\">\n" +
                    "        <h1>Email Verification</h1>\n" +
                    "    </div>\n" +
                    "\n" +
                    "    <!-- Content -->\n" +
                    "    <div class=\"content\">\n" +
                    "        <p>Hello " + fullName +",</p>\n" +
                    "        <p>Thank You for using our   <strong>Qlock-In</strong> Application Decagon Institute. <br/> Click the button below to <strong>RESET</strong> your <strong>PASSWORD</strong>  </p>\n" +
                    "        <a href="+ link +" class=\"btn\">Reset Password</a>\n" +
                    "        <p>If you did not initiate the process, please ignore this email.</p>\n" +
                    "    </div>\n" +
                    "\n" +
                    "    <!-- Footer -->\n" +
                    "    <div class=\"footer\">\n" +
                    "        <p>This email was sent from &copy;<b>Qlock-In Team</b>.</p>\n" +
                    "    </div>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }

