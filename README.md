REPORTS GENERATOR
This is a spring boot application which connects to a relational database and generate reports in a regular intervals of time.

Configurations needed
1. create 3 tables based on the model provided in the model class.
2. initial inserts are required in to the tables named such as scheduled_job , report, scheduled_job_frequency.

Based on the meta data provided in these 3 tables metioned in step 1 and 2,  the reports generator extracts the data from the respective tables or views and 
generates a pdf report. The generated reports will be mailed to the respective mail accounts provided in the meta data mentioned above. 👍
