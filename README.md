# HamdenRentalSystem_tg_bot
Telegram bot for equipment rental including SQLite database, also using Lombok.

Here's a list of it's functions:
![functions](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/e2898970-9dc1-4acb-8af1-d3791939c959)

There's a model of my database.
![schema](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/5a992705-d980-46d2-84dd-44bedf245a99)
Here's how i did create them.(foreign keys were added manually in DBbrowser):
[creating tables.txt](https://github.com/nedmah/HamdenRentalSystem_tg_bot/files/11517258/creating.tables.txt)
[inserting data.txt](https://github.com/nedmah/HamdenRentalSystem_tg_bot/files/11517259/inserting.data.txt)

The Tool table is initially filled, but later the ability to delete or add data is present among the bot functions:
![tool](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/3ed25ee9-2ff2-4283-932b-22aa1ded0c60)

These are sample tables filled in while programming the bot:
1) Orderr - summary is the total price of order, receiving includes two options (delivery or pickup), address can be null if it is a pickup, and ofc id of user who made the order
![orderr](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/510bf168-fdd8-4c86-a352-202ca1c8b05b)
2) Userr - i think there's no need of explanation. Just login and password;
![userr](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/1c962f01-28d8-4c1d-a11b-d5e4283a4745)
3) tool_order - id's of the tools in the catalog and id's of the users who ordered them
![tool_order](https://github.com/nedmah/HamdenRentalSystem_tg_bot/assets/114877544/7959e44f-88fe-4921-8135-1a4e233bc563)
