# Android-SymTrax
SymTrax is an app that matches symptoms to emotions. It uses a sqlite database to store symptoms and daiy tracking information. ListView is used to provide a list of Symptoms, Emotions, and Dates. Emotions are hardcoded into the program while Emotions and Dates are stored in the DB.The SymTrax list can be sorted on Symptom, Emotion and Date.
AddEditSymTraxActivity is implemented in ScrollView and allows for the user to edit or update their database. DatePickerFragment and TimePickerFragment allows users to choose date and time for AddEditSymtraxActivity.
Symptoms have a separate ListView and databae table and can be edited, updated and deleted also. Symptoms are not allowed to be deleted if the user has a record with that particular symptom in it. Updates are cascaded into the user table.
There was an attempt to add a provision for filtering but it was never implemented. Filtering code is either comment out or unreachable but left in place.
SymTrax implements a LoaderManager and uses SymTraxProvider to extend ContentProvider.
Android API is 28 and no AndroidX modules are used. 
