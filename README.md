# Adaptive Merging of Video Processing Tasks (Micro-Service Requests)
This project includes the simulation for adaptive task merging of video processing services.
In this project, the video transcoding simulator is set to work in simulation mode. 
The project simulate the video transcoding systems that receive requests at a predefined time stated in selected 'benchmark input'. The exact configuration of the system can be change by the configuration files in the config folder. The evaluation result (how many tasks meet/ missing the deadlines, execution time, etc.) are saved in the resultstat folder. The results can be processed with scripts prepared within the same folder.  
Check run.sh for example simulations.


If you use this code please cite the following publications:
-
Chavit Denninnart, Mohsen Amini Salehi, “Leveraging Computational Reuse to Enable Cost- and QoS-Efficient Serverless Cloud Computing”, submitted to IEEE Transactions on Parallel and Distributed Systems (TPDS), May 2020

Chavit Denninnart, Mohsen Amini Salehi, Adel N. Toosi, Xiangbo Li, “Leveraging Computational Reuse for Cost- and QoS-Efficient Task Scheduling in Clouds”, in Proceedings of the 16th International Conference on Service-Oriented Computing (ICSOC ’18), Hangzhou, China, Nov. 2018

