 M=load('f:/graph.txt');
 transM=load('f:/transformMatrix.txt');
 pref=load('f:/preference.txt');
 
 [n,n]=size(M);
 
 for j = 1:n
   L{j} = find(G(:,j));
   c(j) = length(L{j});
 end
 
 p=0.85;
 delta=pref;
 x=ones(n,1)/n;
 z=zeros(n,1);
 cnt=0;
 
 while max(abs(x-z)) > .0001
   z = x;
   x = zeros(n,1);
   for j = 1:n
      if c(j) == 0
         x = x + z(j)/n;%转移到任意一个网页
      else
         x(L{j}) = x(L{j}) + z(j)/c(j);%将上次的pagerank值平摊给所有指向的网页
      end
   end
   x = p*x + delta;
   cnt = cnt+1;
end