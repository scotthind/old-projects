����   1 �
 ) t u
  t	 ( v	 ( w	 ( x y z	 ( {	 ( |	 ( } y ~ y  �
  � �
  t �
  � �
  � y � � � � � �
  �
  �
  �?��F�R�9@       
 � �
 � �
 � �
 � �
 � �@��     	 ( � � � intersections Ljava/util/List; 	Signature 'Ljava/util/List<Lsafestpath/Location;>; 	roadWidth I 	roadSpeed distance roadName Ljava/lang/String; points weight D <init> ()V Code LineNumberTable LocalVariableTable this Lsafestpath/Road; (Ljava/util/List;)V theList LocalVariableTypeTable *(Ljava/util/List<Lsafestpath/Location;>;)V 	setPoints getIntersections ()Ljava/util/List; )()Ljava/util/List<Lsafestpath/Location;>; 	getPoints addIntersection (Lsafestpath/Location;)V loc Lsafestpath/Location; addPoint getRoadSpeed ()I setRoadSpeed (I)V getRoadWidth getRoadName ()Ljava/lang/String; setRoadName (Ljava/lang/String;)V name equals (Lsafestpath/Road;)Z r i rSize thisSize toString i$ Ljava/util/Iterator; result #org.netbeans.SourceLevelAnnotations Ljava/lang/Override; 	getWeight ()D 	setWeight (D)V findDistance lat2 lon2 lat1 lon1 dlong dlat a c d dist setDistance 
SourceFile 	Road.java 7 8 java/util/ArrayList * + 4 + 5 6 � � � 0 / . / 2 3 � M � � safestpath/Location V � java/lang/StringBuilder The Intersections of  � � 
 \ R � � � � � � � -- � � � c � c � � � � � � � � � � � 1 / safestpath/Road java/lang/Object java/util/List add (Ljava/lang/Object;)Z size get (I)Ljava/lang/Object; (Lsafestpath/Location;)Z append -(Ljava/lang/String;)Ljava/lang/StringBuilder; iterator ()Ljava/util/Iterator; java/util/Iterator hasNext ()Z next ()Ljava/lang/Object; -(Ljava/lang/Object;)Ljava/lang/StringBuilder; getLatitude getLongitude java/lang/Math sin (D)D pow (DD)D cos sqrt atan2 ! ( )     * +  ,    -  . /    0 /    1 /    2 3    4 +  ,    -  5 6     7 8  9   Z      *� *� Y� � *� Y� � *� �    :              !  # ;         < =    7 >  9   X     
*� *+� �    :       *  , 	 / ;       
 < =     
 ? +  @       
 ? -  ,    A  B >  9   P     *+� �    :   
    2  3 ;        < =      4 +  @        4 -  ,    A  C D  9   /     *� �    :       7 ;        < =   ,    E  F D  9   /     *� �    :       < ;        < =   ,    E  G H  9   D     *� +�  W�    :   
    A  B ;        < =      I J   K H  9   D     *� +�  W�    :   
    F  G ;        < =      I J   L M  9   /     *� �    :       M ;        < =    N O  9   >     *� �    :   
    Q  R ;        < =      0 /   P M  9   /     *� 	�    :       W ;        < =    Q R  9   /     *� 
�    :       \ ;        < =    S T  9   >     *+� 
�    :   
    a  b ;        < =      U 3   V W  9   �     L+� �  >*� �  6=� 1� ++� �  � *� �  � � � ������    :       g 
 h  i " k B m D i J p ;   4    L < =     L X =   5 Y /  
 B Z /   7 [ /   \ R  9   �     [� Y� � *� 
� � � L*� �  M,�  � -,�  � N� Y� +� � -� � � L���+�    :       v  w 9 y V z Y { ;   *  9  I J  & 3 ] ^    [ < =    ? _ 3  `     a    b c  9   /     *� �    :        ;        < =    d e  9   >     *'� �    :   
    �  � ;        < =      5 6   f c  9  �     �H>*� �  d� �*� `�  � � 9*� `�  � � 9*� �  � � 9*� �  � � 9

g k9g k9 o�   � ! k� " k� "k o�   � !kc9 � #g� #� $k9 %k9'c\H�'�    :   6    �  �  � ' � ; � M � _ � j � u � � � � � � � � � ;   z  ' � g 6  ; � h 6  M � i 6  _ o j 6 
 j d k 6  u Y l 6  � $ m 6  �  n 6  �  o 6   � Y /    � < =    � p 6   q O  9   >     *� '�    :   
    �  � ;        < =      1 /   r    s