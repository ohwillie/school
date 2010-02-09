function [x y s] = find_faces(avgface, eigfaces, img, n, scales)

[ri ci di] = size(img);
[ra ca da] = size(avgface);

mses_neg = java.util.PriorityQueue;
mse2coord = java.util.HashMap;

sub_img = [];
  
for i = scales
  scaled_img = my_imresize(img, ri / i, ci / i);
  for j = 1:ri
    for k = 1:ci
      try
        sub_img = scaled_img(j:j + ra - 1, k:k + ca - 1);
      catch
        continue;
      end
      
      sub_img = sub_img / norm(sub_img);
      mse = is_face(avgface, eigfaces, sub_img) *...
            norm(sub_img - avgface) /...
            var(var(sub_img));
        
      mses_neg.add(-mse);
      mse2coord.put(-mse, [k; j; i]);
    end
  end
end

extra = java.util.PriorityQueue;
saved_coords = [];
while ~mses_neg.isEmpty
  mse = mses_neg.remove;
  coord = mse2coord.get(mse);
  saved_coords = cat(2, saved_coords, coord);
  while ~mses_neg.isEmpty 
    this_mse = mses_neg.remove;
    this_coord = mse2coord.get(this_mse);
    if ~overlapping(coord, this_coord, [ca ra])
      extra.add(this_mse);
    end
  end
  tmp = mses_neg;
  mses_neg = extra;
  extra = tmp;
end
[r n] = size(saved_coords);
x = zeros(1, n); y = zeros(1, c); s = zeros(1, c);
for i = 1:n
  x(i) = saved_coords(1, i);
  y(i) = saved_coords(2, i);
  s(i) = saved_coords(3, i);
end

end
