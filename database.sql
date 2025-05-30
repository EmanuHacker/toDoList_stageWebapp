PGDMP     -    /                |           postgres    12.20    12.20                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    13318    postgres    DATABASE     �   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'Italian_Italy.1252' LC_CTYPE = 'Italian_Italy.1252';
    DROP DATABASE postgres;
                postgres    false                       0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    2843                        2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
                postgres    false                       0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                   postgres    false    4            �            1259    16399    attivita    TABLE     �   CREATE TABLE public.attivita (
    identificatore integer,
    testo character varying,
    priorita character varying(32),
    owner character varying(255),
    descrizione character varying,
    stato character varying
);
    DROP TABLE public.attivita;
       public         heap    postgres    false    4            �            1259    24625    corrispondenza    TABLE     �   CREATE TABLE public.corrispondenza (
    riga integer,
    attivita integer,
    mittente character varying,
    testo character varying,
    "time" bigint
);
 "   DROP TABLE public.corrispondenza;
       public         heap    postgres    false    4            �            1259    16402    intersezione    TABLE     t   CREATE TABLE public.intersezione (
    name character varying(255),
    identificatore integer,
    riga integer
);
     DROP TABLE public.intersezione;
       public         heap    postgres    false    4            �            1259    24585 	   messaggio    TABLE     �   CREATE TABLE public.messaggio (
    testo character varying(255) NOT NULL,
    letto boolean DEFAULT false NOT NULL,
    riga integer,
    "time" bigint
);
    DROP TABLE public.messaggio;
       public         heap    postgres    false    4            �            1259    16393    utenti    TABLE     �   CREATE TABLE public.utenti (
    mail character varying(255),
    name character varying(255),
    pswd character varying(255),
    role character varying DEFAULT 'USER'::character varying NOT NULL
);
    DROP TABLE public.utenti;
       public         heap    postgres    false    4                      0    16399    attivita 
   TABLE DATA           ^   COPY public.attivita (identificatore, testo, priorita, owner, descrizione, stato) FROM stdin;
    public          postgres    false    204   `                 0    24625    corrispondenza 
   TABLE DATA           Q   COPY public.corrispondenza (riga, attivita, mittente, testo, "time") FROM stdin;
    public          postgres    false    207   �                 0    16402    intersezione 
   TABLE DATA           B   COPY public.intersezione (name, identificatore, riga) FROM stdin;
    public          postgres    false    205   �                 0    24585 	   messaggio 
   TABLE DATA           ?   COPY public.messaggio (testo, letto, riga, "time") FROM stdin;
    public          postgres    false    206   �                 0    16393    utenti 
   TABLE DATA           8   COPY public.utenti (mail, name, pswd, role) FROM stdin;
    public          postgres    false    203   �          1  x���_k� şo>��ۺ��Rp!������ƍq�Ѣ���3˒��L_=�s�x	�T�C�[��h�`�^dy��Y��xt!��dO�[�Z�U2� �_�2Mq�+�wY��tt��s�F���9�4͊�<���v��4��u!
3!69�߭�vj(}�E����^�����G����d��Z�x�`��	�-p)��b�&Ǌ�)�݀B���P���B&�:u[��ڇ���[�m�!��*[L:>e��tB]+�B:Uc�M���+��](FtH%���O{�h����gI�|�>�            x������ � �         $  x�MQ�n�0�ɏ)��_c
Y h�.�-�ː��/I�i�;��O�C��¯0'h���}p���E{�iI����ۀ��n)N	,�2�A���-�'��)�\U��Y�HMk�=0l�R�˨*uT.Ş����I4N��R]�"Ղ����-���kxaջ�6!-^����Dmfo�����������e��ƫ�x��e1g�LÏ�aj6��UH#q��c�⍌�dHh��H��,9a�úUx��t\hu	�H��/��$ރ�_v`���
��auk�~C�_s���         �  x���͎�0���SX\�!�ՂVZ>ˍ�I��R6�g<Gރc�Ʃ+�m\��R�z~��=3��w�E�C~��~t���]�UA��q�5���c�tg��e�RR��v�����kX��֢v�-�޻g���
f&�I�B�����^������������)A�+$<�����v��@؃�dل�p��C���ۇ���&��Bk�ޗw�~_�)��-͵�g��� bBH�03�g 6�ݺ6�q��F@:EFX��X����
�xyp�(T4C)��+tJ 1f]8�Y �m�zNp��k|�네"��b����u�
U�Ӿ�'Dյh���)iK��F�r��e�y�&�O`z<wՉ��
� ��*u�)M�|o{��͘�唧�ٴ[���:��n�*�\��me۟����C�'��'�?B��F�m�u�u�]W(�7��.��+�Q|[�9{�!R�`'��I��������:'g��Lh����}@7?�������4��"_J��G��g}�&B��8�;��ا����k&'��/��µ�DӵJsc��n�>���|��)�f���$���'�e4�C�wE0e(����#�K�צ Z�Ѕ�8a)Ae�a?��Q 畞��u���Q���T&��E�����H�Ƒ�c@�^���s?Bk38ZsZ��{ߏ��bT�<i�$m2Gq� !)$�D����R*Iy�l�l�R�����Lb�S�W���׻��[���C�ϋ#e���绷w_gi0�0�"�d�enFTc���2-*��]�!S!�u!j�Y{�y��{d��Wݾk�BIN(k+%��4�s�u3�8��H�Y
Qb��|1Ӡ��kqrp�R��Ƹf��qLI���]!q:@����,˿5�c         6  x���ɶ�H��u���	� �APOo�I&S���rK�e�`�?�|�gdDd�U���?a��)�����~�&��#�7rj+M�27T�� �����1�uΙUM9M�2����Jg'�/�W��/7��'4�/슷����*�����q͝�J"����櫄A縘4r�������u�D~�_���g��̍�0.P�b7������l�֞ܮ�z=��~� �K�n����������u������o��|����l�ˏD�*~�zx3;C�����_V�7���=��왭ܕϤ7���@+`�1|��vW>T7�J�ӱF�n$<��E�R0� ���*S�j�c�B�I�4��x=*+��,��z[�P�g��|ݦ	�.���ھ���l,����,��ܴ�lÜ��o}��3�.t*5����w��p 8����"����j9KG;u�l �d���%|�ߤ�Ά�>i	6l"��QD=r�
Zx�pRøB�14F�]�OD�rH���[�/!���b��d�89gϫ2���am�-)T����kE�P������p��^z�xW6?�;!���I�O�����f��X�T�+��V!D6�[r���#���[��~�~������M� K�-K�>��t:�f4VSUi�B����L��Л���娉����"?��ӛ2��M��e{�]^����#��t0�.@6���F{k��j�Q�b��0�;�k��/����Y�p��|zk�s-�J`ו��ЛE�	��*v��3�(�rFރky_���͠|Bqb>k�i�b�	 ����1��U�m)u�����5�Gl�U��96we`#SG�n]�TN<�Du��k�]�?���\�K�O�d�C:�U���W��5!��X>%�?Ŕ���l���$��Cx!J�sNi�5��䴪"��nRI��[G|H]�I�p!wnV�r#�� }<�T��T@-*O�Z�E�E����a�\�����ܕN/Ax��a�����ZJg��\�aHxL�Ƶ�΋�#;J`|��g�~0�k��߳�����^C�ݕ.OE�I[1��H�l�:;��uz�g�������V�yQ>��?I߻�ߵ����G�8��y@�5�����u����N�E��Iq|�Wk����k�(�/�Z�t����t���8���V�N�����t��-ك�t5�TβŖ4�p�8�h3n�V/��~WUc??U�HIiʰR�%N�Bܶ�9��]�}]�i�ΰsq��\���`��O��ȝ8E�$s-���KV��� �r�_a��8�ih�Jdc��?ɣ�}\�����?Ab(�     