<?php

$JAVA_HOME = "C:/Program Files/Java/jdk1.8.0_25";
$PATH = "$JAVA_HOME/bin:".getenv('PATH');
putenv("JAVA_HOME=$JAVA_HOME");
putenv("PATH=$PATH");

	extract($_POST);
	
	if(strcmp($file_path, 'C:/Software/xampp/htdocs/glint/resources/question.txt') == 0)
	{
		#set_time_limit(60);
		writeToFile($file_path , $file_content);
		//RUN SIMILARITY CODE:
		$status = runSimilarity();
		if($status == 0)
			readFromAnswerFile();;
	}
	else if(strcmp($file_path, 'C:/Software/xampp/htdocs/glint/resources/passage.txt') == 0)
	{
		#set_time_limit(300);
		writeToFile($file_path , $file_content);
		$status = runSimplifyAndQuesGen();
	}
	
	function writeToFile($file_path , $file_content){
		if($file_path && $file_content){
			$my_file = $file_path;
			$handle = fopen($my_file, 'w') or die('Cannot open file:  '.$my_file);
			fwrite($handle, $file_content);
		}
		
	}
	
	function readFromAnswerFile(){
		$my_file = "C:/Software/xampp/htdocs/glint/resources/answer.txt";
		$handle = fopen($my_file, 'r') or die('Cannot open file:  '.$my_file);
		
		if(filesize($my_file)>0)
			echo fread($handle, filesize($my_file ));
		
		fclose($handle);
	}
	
	
	function runSimilarity(){
		
		echo shell_exec("py C:/Software/xampp/htdocs/glint/php/MC/similarity.py");

	}
	
	//RUN JAVA CODE -> APP1.py:	
	function runSimplifyAndQuesGen(){
	
		echo shell_exec("java -jar C:/Software/xampp/htdocs/glint/php/SentenceSimplification1/out/artifacts/SentenceSimplification1_jar/SentenceSimplification1.jar 2>&1");
	
	}
	
?>