function [C] = read_images()
try
  cd ~/Dropbox/school/project2
  C = cell(72, 1);
  cd class_images/smiling_cropped;
  files = dir('*.jpg');
  for i = 1:36
    C{i} = rgb2gray(double(imread(files(i).name)) / 255);
  end
  cd ../nonsmiling_cropped;
  for i = 37:72
    C{i} = rgb2gray(double(imread(files(i - 36).name)) / 255);
  end
  cd ../..;
catch
  cd ~/Dropbox/school/project2
end
end