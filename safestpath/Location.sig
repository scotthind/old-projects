����   1 W
  3
  4
  5	  6	  7
 8 9@V�     	 : ; <
 = >	  ?@f�      @ A
  3 B
  C
  D E F
  G H I latitude D 	longitude altitude <init> (DD)V Code LineNumberTable LocalVariableTable this Lsafestpath/Location; (DDD)V getLatitude ()D setLatitude (D)V getLongitude setLongitude toString ()Ljava/lang/String; equals (Lsafestpath/Location;)Z loc 
SourceFile Location.java  J ( ) + )     K L M N O P *Valid latitude ranges from -90.00 to 90.00 Q R S   -Valid longitude ranges from -180.00 to 180.00 java/lang/StringBuilder [ T U T V , ] , - safestpath/Location java/lang/Object ()V java/lang/Math abs (D)D java/lang/System err Ljava/io/PrintStream; java/io/PrintStream println (Ljava/lang/String;)V append -(Ljava/lang/String;)Ljava/lang/StringBuilder; (D)Ljava/lang/StringBuilder; !                              b     *� *'� *)� *� �    !          	       "         # $                 %      m     *� *'� *)� *� �    !       *  + 	 ,  -  . "   *     # $                      & '      /     *� �    !       1 "        # $    ( )      \     '�  �� *'� � � 	
� �    !       5  7  ;  = "        # $          * '      /     *� �    !       @ "        # $    + )      \     '�  �� *'� � � 	� �    !       D  F  J  L "        # $          , -      ^     4� Y� � *� � � *� � � *� � � � �    !       P "       4 # $    . /      ^     **� +� �� *� +� �� *� +� �� � �    !       U "       * # $     * 0 $   1    2