LAB 3
Prabod Rathnayaka 140520G
Menuka Warushavithana 140650E


How to Compile.

run compile.sh or javac -sourcepath . Entry.java

How to Run

This solution rely on a logical clock. (simulation ticks) which can simulate time. we can set the ratio between seconds to simulation ticks.
ex: ratio = 100 means in 1 second logical clock ticks 100 times.(1 sec = 100 logical sec)


java Entry BusArrivalMeanTimeInMinutes RiderArrivalMeanTimeInSeconds secondsToTickRatio

ex: java Entry 20 30 1000

or simply execute run.sh.

To terminate use CTRL+C