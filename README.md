# Coffee-Roasting-Assistant
&emsp; This project was to develop a mobile Android app with the purpose of assisting in professional
and hobby coffee roasting. The app stores information relevant to the user’s specific roast profiles.
It is set up with the roaster’s temperature readout display centered on the app’s camera display
where the app then uses image recognition to automate temperature data collection during a
roasting session. This information is graphed and can be viewed as it changes, as well as reloaded at
any time by the user. These graphs can be overlayed to compare changes in bean quality or to
visualize the outcome of changing roasting parameters.
## Essential Problems
The problems which this project aimed to solve are:
<ul>
  <li>Recording second to second temperature data and storing that information.
  <li>Graphing temperature over time of a coffee roast.
  <li>Automating data collection.
  <li>Storing information pertaining to roasting.
  <li>Backing up or sharing roast information.
</ul>

## Image Recognition Neural Network
&emsp;  The image recognition in this project was to be done in one of two ways. Firstly, attempting to
design an image recognition system from scratch creating a convolutional neural network using no
external libraries. If this was not achievable the project was to fall back on a premade image
recognition or machine learning library. Regardless of which option would be used it would provide
an excellent learning opportunity to dive into the intricate details of how convolutional neural
networks functioned. Fortunately, I was able to develop a functional system which provided an
acceptably accurate recognition and data recording, and so no external image recognition or
machine learning libraries were utilized.<br>
&emsp;  The image recognition system in this project involves two separate programs: a 
<a href="https://github.com/robertnmoffat/ConvolutionNeuralNetworkTrainer">desktop program</a> 
to be used in development to train image recognition models written in C#, and the main Android
app which uses the trained models to translate image data to temperature values over time. The
desktop training program will only be used by myself during development, and in future if any
updates are needed to the neural network models. This program uses backpropagation to train the
models and then after every epoch of training automatically writes to disk a custom filetype which is
then to be copied over to the Android app to use in live image recognition.<br><br>
More information about the development of the image recognition portion of this project is available 
in the separate <a href="https://github.com/robertnmoffat/ConvolutionNeuralNetworkTrainer">training program repo.</a> 

## Server
&emsp;  Contained within this project are the files for the NodeJS server. This server is used to share and store 
relevant roasting information externally. This uses a REST API to transfer JSON data between the Android app and 
the server. This data is different bean, roast, and blend information entered and saved by the user.
