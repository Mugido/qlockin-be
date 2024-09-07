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


    public static String addEmployeeEmailBody(String fullName, String employeeId, String password, String link) {

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Email Verification</title>\n" +
                "    <style>\n" +
                "        body, h1, p {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            font-family: 'Inter', serif;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "\n" +
                "        .email-wrapper {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "            box-shadow: 1px 1px 2px #6A0DAD;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #6A0DAD;\n" +
                "            color: #FFFFFF;\n" +
                "            text-align: center;\n" +
                "            padding: 20px 0;\n" +
                "            border-radius: 8px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "            background-color: #FFFFFF;\n" +
                "            border-radius: 8px;\n" +
                "        }\n" +
                "\n" +
                "        .btn {\n" +
                "            display: inline-block;\n" +
                "            background-color: #FFFFFF;\n" +
                "            color: black;\n" +
                "            text-decoration: none;\n" +
                "            padding: 10px 20px;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 20px;\n" +
                "            margin-bottom: 20px;\n" +
                "            box-shadow: 1px 1px 1px 2px #6A0DAD;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: left;\n" +
                "            margin-top: 20px;\n" +
                "            padding-left: 20px;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "\n" +
                "        @media screen and (max-width: 600px) {\n" +
                "            .email-wrapper {\n" +
                "                width: 100%;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"email-wrapper\">\n" +
                "    <div class=\"header\">\n" +
                "        <h1>Welcome to Qlock-in!</h1>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"content\">\n" +
                "        <p>Hello " + fullName + ",</p>\n" +
                "        <p>We are excited to have you join us at Qlock-in! To get started, please use the following credentials to log in to your account:</p>\n" +
                "        <p><strong>Employee ID:</strong> " + employeeId + "</p>\n" +
                "        <p><strong>Password:</strong> " + password + "</p>\n" +
                "        <p>Please click the link below to Register your pass key. Which will enable you to clock in and out with your device.</p>\n" +
                "        <a href=\"" + link + "\" class=\"btn\">Login</a>\n" +
                "        <p>If this message is not meant for you, please ignore this email.</p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"footer\">\n" +
                "        <p>Thank you,<br>&copy;<b>Qlock-in Team</b>.</p>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }
    }

