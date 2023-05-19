# HamdenRentalSystem_tg_bot
Telegram bot for equipment rental including SQLite database, also using Lombok.

Here's a list of it's functions:
![functions](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/3f4b933b-0e1e-49d4-abe2-dfc52458e9c9)


There's a model of my database.
![schema](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/3fac4372-0633-4dd6-8821-11ad86883caf)
Here's how i did create them.(foreign keys were added manually in DBbrowser):
[creating tables.txt](https://github.com/nedmah/HamdenRentalSystem_tg_bot/files/11518361/creating.tables.txt)
[inserting data.txt](https://github.com/nedmah/HamdenRentalSystem_tg_bot/files/11518362/inserting.data.txt)

The Tool table is initially filled, but later the ability to delete or add data is present among the bot functions:
![tool](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/343024c3-2a9f-49ac-a26c-2fbe42fb46e2)


These are sample tables filled in while programming the bot:
1) Orderr - summary is the total price of order, receiving includes two options (delivery or pickup), address can be null if it is a pickup, and ofc id of user who made the order
![orderr](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/f24eb4ed-fa2e-44a7-8883-350de82162b6)
2) Userr - i think there's no need of explanation. Just login and password;
![userr](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/fccc05a0-b2d0-4af0-aa3c-7e05411fb3d3)
3) tool_order - id's of the tools in the catalog and id's of the users who ordered them
![tool_order](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/91653423-c09d-46de-b06b-82d9883d08cb)
